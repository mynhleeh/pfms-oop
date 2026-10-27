"""Merge and split markdown files on level-2 (##) headings."""

import argparse
import os
import re
import sys
import unicodedata
from dataclasses import dataclass
from pathlib import Path

PREAMBLE_NAME = "00-preamble.md"
HEADING_PREFIX = "## "
_LEADING_NUMBER = re.compile(r"^\d+(?:\.\d+)*\.?(?:\s+|$)")
_FENCE_LINE = re.compile(r"^ {0,3}(`{3,}|~{3,})(.*)$")


class MdsecError(Exception):
    pass


def slugify(heading_text: str) -> str:
    text = _LEADING_NUMBER.sub("", heading_text.strip())
    text = text.replace("đ", "d").replace("Đ", "D")
    decomposed = unicodedata.normalize("NFD", text)
    unaccented = "".join(ch for ch in decomposed if not unicodedata.combining(ch))
    return re.sub(r"[^a-z0-9]+", "-", unaccented.lower()).strip("-")


def _trim_blank_lines(lines: list[str]) -> str:
    start, end = 0, len(lines)
    while start < end and not lines[start].strip():
        start += 1
    while end > start and not lines[end - 1].strip():
        end -= 1
    return "\n".join(lines[start:end]) + "\n" if start < end else ""


def _fence_opener(line: str) -> str:
    match = _FENCE_LINE.match(line)
    if not match:
        return ""
    marker, info = match.groups()
    return "" if marker[0] == "`" and "`" in info else marker


def _closes_fence(line: str, opener: str) -> bool:
    match = _FENCE_LINE.match(line)
    if not match:
        return False
    marker, rest = match.groups()
    return marker[0] == opener[0] and len(marker) >= len(opener) and not rest.strip()


def _group_by_heading(text: str) -> tuple[list[str], list[list[str]]]:
    preamble: list[str] = []
    sections: list[list[str]] = []
    open_fence = ""
    for line in text.split("\n"):
        if open_fence:
            if _closes_fence(line, open_fence):
                open_fence = ""
        elif _fence_opener(line):
            open_fence = _fence_opener(line)
        elif line.startswith(HEADING_PREFIX):
            sections.append([])
        (sections[-1] if sections else preamble).append(line)
    return preamble, sections


def plan_split(text: str) -> list[tuple[str, str]]:
    if not text.strip():
        raise MdsecError("nothing to split: input is empty")
    preamble_lines, section_lines = _group_by_heading(text)
    width = max(2, len(str(len(section_lines))))
    plan: list[tuple[str, str]] = []
    preamble = _trim_blank_lines(preamble_lines)
    if preamble:
        plan.append((PREAMBLE_NAME, preamble))
    seen: dict[str, str] = {}
    for position, lines in enumerate(section_lines, start=1):
        heading = lines[0][len(HEADING_PREFIX):]
        slug = slugify(heading)
        if not slug:
            raise MdsecError(f"heading has an empty slug: {lines[0]!r}")
        if slug in seen:
            raise MdsecError(f"duplicate slug {slug!r}: {seen[slug]!r} and {lines[0]!r}")
        seen[slug] = lines[0]
        plan.append((f"{position:0{width}d}-{slug}.md", _trim_blank_lines(lines)))
    return plan


def _part_prefix(path: Path) -> int | None:
    prefix = re.match(r"^(\d+)", path.name)
    if path.is_file() and path.suffix == ".md" and prefix:
        return int(prefix.group(1))
    return None


def list_parts(folder: Path) -> list[Path]:
    numbered = []
    merged_name = f"{folder.resolve().name}.md"
    for path in folder.iterdir():
        prefix = _part_prefix(path)
        if prefix is not None and path.name != merged_name:
            numbered.append((prefix, path.name, path))
    if not numbered:
        raise MdsecError(f"no numbered .md files in {folder}")
    return [path for _, _, path in sorted(numbered)]


def join_parts(contents: list[str]) -> str:
    trimmed = [_trim_blank_lines(content.split("\n")).rstrip("\n") for content in contents]
    non_blank = [content for content in trimmed if content]
    if not non_blank:
        raise MdsecError("nothing to merge: every part is empty")
    return "\n\n".join(non_blank) + "\n"


def _normalized(path: Path) -> Path:
    resolved = str(path.resolve())
    if resolved.startswith("\\\\?\\"):
        resolved = resolved[4:]
    return Path(os.path.normcase(resolved))


@dataclass
class Operation:
    writes: list[tuple[Path, str]]
    sources: list[Path]


def _read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8-sig")
    except UnicodeDecodeError:
        raise MdsecError(f"not valid UTF-8: {path}") from None


def _check_target(target: Path, no_overwrite: bool) -> None:
    if target.exists() and not target.is_file():
        raise MdsecError(f"target is not a file: {target}")
    if target.exists() and no_overwrite:
        raise MdsecError(f"target exists (--no-overwrite given): {target}")
    existing = next((path for path in target.parents if path.exists()), None)
    if existing is not None and not existing.is_dir():
        raise MdsecError(f"output location is not a folder: {existing}")


def _execute(operation: Operation, no_overwrite: bool, dry_run: bool) -> list[str]:
    source_keys = {_normalized(path) for path in operation.sources}
    lines = []
    for target, _ in operation.writes:
        if _normalized(target) in source_keys:
            raise MdsecError(f"output would overwrite its own source: {target}")
        _check_target(target, no_overwrite)
        lines.append(f"{'OVERWRITE' if target.exists() else 'CREATE'} {target}")
    if dry_run:
        return lines
    for target, content in operation.writes:
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(content, encoding="utf-8", newline="\n")
    return lines


def _stale_warnings(target_dir: Path, planned_names: set[str], source: Path) -> list[str]:
    if not target_dir.is_dir():
        return []
    source_key = _normalized(source)
    stale = sorted(
        path for path in target_dir.iterdir()
        if _part_prefix(path) is not None
        and path.name not in planned_names
        and _normalized(path) != source_key
    )
    return [f"WARN stale part file not in this split, left untouched: {path}" for path in stale]


def run_split(source: Path, output: Path | None, no_overwrite: bool, dry_run: bool) -> list[str]:
    target_dir = output if output is not None else source.parent
    if not source.is_file():
        raise MdsecError(f"not a file: {source}")
    plan = plan_split(_read_text(source))
    operation = Operation([(target_dir / name, content) for name, content in plan], [source])
    lines = _execute(operation, no_overwrite, dry_run)
    return lines + _stale_warnings(target_dir, {name for name, _ in plan}, source)


def run_merge(source: Path, output: Path | None, no_overwrite: bool, dry_run: bool) -> list[str]:
    target_dir = output if output is not None else source
    if not source.is_dir():
        raise MdsecError(f"not a folder: {source}")
    parts = list_parts(source)
    merged = join_parts([_read_text(part) for part in parts])
    operation = Operation([(target_dir / f"{source.resolve().name}.md", merged)], parts)
    return _execute(operation, no_overwrite, dry_run)


def _build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(prog="mdsec", description=__doc__)
    parser.add_argument("source", help="folder to merge, or file to split")
    modes = parser.add_subparsers(dest="mode", required=True)
    for mode in ("merge", "split"):
        sub = modes.add_parser(mode)
        sub.add_argument("--output", help="folder that receives the result")
        sub.add_argument("--no-overwrite", action="store_true", help="fail if a target already exists")
        sub.add_argument("--dry-run", action="store_true", help="print the plan only")
    return parser


def main(argv: list[str] | None = None) -> int:
    args = _build_parser().parse_args(argv)
    runner = run_split if args.mode == "split" else run_merge
    output = Path(args.output) if args.output else None
    try:
        lines = runner(Path(args.source), output, args.no_overwrite, args.dry_run)
    except (MdsecError, OSError) as error:
        print(f"mdsec: {error}", file=sys.stderr)
        return 1
    print("\n".join(lines))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
