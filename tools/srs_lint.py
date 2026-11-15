"""Count-based lint for the PFMS SRS markdown files (checks A1-A5, B2-B6, B9, B10, B13, C1, C3, C4).

B10 verifies Standalone Bibliography: File 11 exists and zero residual in-text citation markers in body.
B13 flags identifiers that still use a retired prefix (RETIRED_PREFIXES) in prose.
C3 only checks global claims; UC claims below 16 are skipped because per-diagram counts are legitimate.
Checks A6-A8, B1, B7, B8, B11, B12, C2, C5-C10 need human or reviewer judgement.
"""

import argparse
import re
import sys
import unicodedata
from collections import defaultdict
from dataclasses import dataclass
from pathlib import Path

DEFAULT_DIR = Path(__file__).resolve().parent.parent / "docs" / "software-requirement-specification"
CITATION_FILE = "10-tai-lieu-trich-dan.md"
TOC_FILE = "00-preamble.md"
MAX_PARAGRAPH_LINES = 8
MAX_RANGE_SPAN = 200
MAX_CONSECUTIVE_TABLES = 2

FENCE = re.compile(r"^ {0,3}(`{3,}|~{3,})")
HEADING = re.compile(r"^(#{1,6})\s+(.*)$")
TABLE_ROW = re.compile(r"^\s*\|")
DELIMITER_CELL = re.compile(r"^\s*:?-{1,}:?\s*$")
LIST_ITEM = re.compile(r"^\s*(?:[-*+]|\d+[.)])\s")
FORBIDDEN_WORDS = [
    "delve", "tapestry", "landscape", "pivotal", "seamless", "leverage", "game-changer",
    "đòn bẩy", "bức tranh tổng thể", "toàn diện và sâu sắc", "trực quan sinh động", "chìa khóa vàng",
]
ID_FORMATS = {
    "UC": r"UC-[A-Z]{3}-\d{2}",
    "FR": r"FR-[A-Z]{3}-\d{2}",
    "NFR": r"NFR-\d{2}",
    "BR": r"BR-[A-Z]{3}-\d{2}",
    "ERR": r"ERR-[A-Z]{3}-\d{3}",
    "AC": r"AC-[A-Z]{3}-\d{2}(?:\.\d+)?",
}
ID_TOKEN = re.compile(r"\b(UC|FR|NFR|BR|ERR|AC)-[A-Z0-9][A-Z0-9.-]*[A-Za-z0-9]*\b")
PLACEHOLDER = re.compile(r"DOMAIN|(?:^|-)NN(?:$|[.-])|XXX|-X\b")
GROUP_PREFIX = re.compile(r"(?:UC|FR|AC|ERR|BR)-[A-Z]{3}")
RANGE = re.compile(r"((?:(?:UC|FR|AC|ERR|BR)-[A-Z]{3}-)|NFR-)(\d+)(?:\.\.|–)(?:\1)?(\d+)")
RETIRED_PREFIXES = ("TC",)
RETIRED_ID = re.compile(r"\b(?:%s)-[A-Z0-9]" % "|".join(RETIRED_PREFIXES))
CHECK_IDS = [
    "A1", "A2", "A3", "A4", "A5", "B2", "B3", "B4", "B5", "B6", "B9", "B10", "B13", "C1", "C3", "C4",
]


@dataclass(frozen=True)
class Finding:
    check: str
    file: str
    line: int
    text: str


@dataclass
class Line:
    number: int
    text: str
    in_code: bool


def load_lines(path: Path) -> list[Line]:
    lines: list[Line] = []
    open_fence = ""
    text = unicodedata.normalize("NFC", path.read_text(encoding="utf-8-sig"))
    for number, raw in enumerate(text.split("\n"), start=1):
        match = FENCE.match(raw)
        if open_fence:
            lines.append(Line(number, raw, True))
            if match and match.group(1)[0] == open_fence[0] and len(match.group(1)) >= len(open_fence):
                open_fence = ""
        elif match:
            open_fence = match.group(1)
            lines.append(Line(number, raw, True))
        else:
            lines.append(Line(number, raw, False))
    return lines


def unclosed_fence(name: str, lines: list[Line]) -> list[Finding]:
    opener = None
    for line in lines:
        match = FENCE.match(line.text) if line.in_code else None
        if not match:
            continue
        if opener is None:
            opener = (match.group(1), line.number)
        elif match.group(1)[0] == opener[0][0] and len(match.group(1)) >= len(opener[0]):
            opener = None
    if opener is None:
        return []
    return [Finding("B2", name, opener[1], "code fence is never closed; later lines are not checked")]


def prose(lines: list[Line]) -> list[Line]:
    return [line for line in lines if not line.in_code]


def quote(text: str, limit: int = 110) -> str:
    flat = text.strip()
    return flat if len(flat) <= limit else flat[: limit - 3] + "..."


def is_delimiter_row(text: str) -> bool:
    return bool(TABLE_ROW.match(text)) and all(DELIMITER_CELL.match(cell) for cell in split_cells(text))


def scan_pattern(check: str, name: str, lines: list[Line], pattern: re.Pattern) -> list[Finding]:
    found = []
    for line in prose(lines):
        text = INLINE_CODE.sub("", line.text)
        if check == "A1":
            text = HTML_COMMENT.sub("", text)
            if is_delimiter_row(text):
                continue
        if TABLE_ROW.match(text):
            text = "|".join(cell for cell in split_cells(text) if cell.strip("* ") != "—")
        found.extend(Finding(check, name, line.number, quote(line.text)) for _ in pattern.finditer(text))
    return found


A1_PATTERN = re.compile(r"[—–]|(?<!-)--(?!-)")
INLINE_CODE = re.compile(r"`[^`]*`")
HTML_COMMENT = re.compile(r"<!--.*?-->|<!--|-->")
MATH_SPAN = re.compile(r"\$[^$]+\$")
A2_PATTERN = re.compile(r"[^\W\d_]+\s*\(\s*[A-Za-z][A-Za-z\- /]*[a-z][A-Za-z\- /]*\)")
A3_PATTERN = re.compile(r"^\s*(?:[-*+]|\d+[.)])\s+(?:\*\*[^*]*:\*\*|\*\*[^*]*\*\*\s*:)")
A4_PATTERN = re.compile(r"không\s+chỉ\s.{0,80}?\smà\s+còn|not\s+only\s.{0,80}?\sbut\s+also", re.IGNORECASE)
LOWER_WORD_BEFORE_PAREN = re.compile(r"^[^\W\d_A-Z][^\W\d_]*$")
A5_PATTERN = re.compile("|".join(r"\s+".join(map(re.escape, word.split())) for word in FORBIDDEN_WORDS), re.IGNORECASE)


def check_a2(name: str, lines: list[Line]) -> list[Finding]:
    found = []
    if name == CITATION_FILE:
        return found
    for line in prose(lines):
        for match in A2_PATTERN.finditer(MATH_SPAN.sub("", INLINE_CODE.sub("", line.text))):
            before, inside = match.group(0).split("(", 1)
            words = before.split()
            numbering = re.fullmatch(r"[ivx]{1,4}|[a-z]", inside.rstrip(")").strip())
            if "/" not in inside and not numbering and words and (LOWER_WORD_BEFORE_PAREN.match(words[-1]) or re.search(r"[À-ỹ]", words[-1])):
                found.append(Finding("A2", name, line.number, quote(match.group(0))))
    return found


def check_a4(name: str, lines: list[Line]) -> list[Finding]:
    found = []
    text_lines = prose(lines)
    for index, line in enumerate(text_lines):
        following = text_lines[index + 1].text if index + 1 < len(text_lines) else ""
        window = INLINE_CODE.sub("", line.text) + " " + following
        if any(match.start() < len(line.text) for match in A4_PATTERN.finditer(window)):
            found.append(Finding("A4", name, line.number, quote(line.text)))
    return found


def check_a3(name: str, lines: list[Line]) -> list[Finding]:
    return [Finding("A3", name, line.number, quote(line.text)) for line in prose(lines) if A3_PATTERN.match(line.text)]


def split_cells(row: str) -> list[str]:
    body = row.strip()
    body = body[1:] if body.startswith("|") else body
    body = body[:-1] if body.endswith("|") and not body.endswith("\\|") else body
    return re.split(r"(?<!\\)\|", body)


def table_blocks(lines: list[Line]) -> list[list[Line]]:
    blocks: list[list[Line]] = []
    current: list[Line] = []
    for line in lines:
        if not line.in_code and TABLE_ROW.match(line.text):
            current.append(line)
        elif current:
            blocks.append(current)
            current = []
    return blocks + ([current] if current else [])


def check_b2(name: str, lines: list[Line]) -> list[Finding]:
    found = []
    for block in table_blocks(lines):
        expected = len(split_cells(block[0].text))
        for row in block[1:]:
            count = len(split_cells(row.text))
            if count != expected:
                found.append(Finding("B2", name, row.number, f"{count} cells, header has {expected}: {quote(row.text, 80)}"))
        if len(block) > 1 and not all(DELIMITER_CELL.match(cell) for cell in split_cells(block[1].text)):
            found.append(Finding("B2", name, block[1].number, "second row is not a delimiter row"))
    return found


SPAN = re.compile(r"`[^`]*`|\$[^$\s][^$]*[^$\s]\$")


def check_b3(name: str, lines: list[Line]) -> list[Finding]:
    found = []
    for line in prose(lines):
        if not TABLE_ROW.match(line.text):
            continue
        for span in SPAN.finditer(line.text):
            if re.search(r"(?<!\\)\|", span.group(0)):
                found.append(Finding("B3", name, line.number, quote(span.group(0))))
    return found


def check_b4(name: str, lines: list[Line]) -> list[Finding]:
    found = []
    for line in prose(lines):
        if TABLE_ROW.match(line.text) and any(cell.strip("* ") == "—" for cell in split_cells(line.text)):
            found.append(Finding("B4", name, line.number, quote(line.text)))
    return found


def check_b5(name: str, lines: list[Line]) -> list[Finding]:
    found = []
    run = 0
    previous_was_table = False
    last_content_was_table = False
    for line in lines:
        is_table = not line.in_code and bool(TABLE_ROW.match(line.text))
        is_blank = not line.text.strip()
        if is_table and not previous_was_table:
            run = run + 1 if last_content_was_table else 1
            if run > MAX_CONSECUTIVE_TABLES:
                found.append(Finding("B5", name, line.number, f"table #{run} in a row without lead-in"))
        if not is_blank:
            last_content_was_table = is_table
            if not is_table:
                run = 0
        previous_was_table = is_table
    return found


def is_paragraph_line(line: Line) -> bool:
    text = line.text
    return bool(
        text.strip()
        and not line.in_code
        and not TABLE_ROW.match(text)
        and not LIST_ITEM.match(text)
        and not HEADING.match(text)
        and not text.startswith("  ")
        and not re.match(r"^(?:-{3,}\s*$|<[A-Za-z/!])", text.lstrip())
    )


def check_b6(name: str, lines: list[Line]) -> list[Finding]:
    found = []
    run: list[Line] = []
    for line in lines + [Line(0, "", False)]:
        if is_paragraph_line(line):
            run.append(line)
            continue
        if len(run) > MAX_PARAGRAPH_LINES:
            found.append(Finding("B6", name, run[0].number, f"{len(run)} consecutive lines: {quote(run[0].text, 70)}"))
        run = []
    return found


def expand_ranges(text: str) -> str:
    def expand(match: re.Match) -> str:
        first, last = int(match.group(2)), int(match.group(3))
        if last < first or last - first > MAX_RANGE_SPAN:
            return match.group(0)
        width = len(match.group(2))
        return " ".join(f"{match.group(1)}{n:0{width}d}" for n in range(first, last + 1))
    return RANGE.sub(expand, text)


def check_b9(name: str, lines: list[Line]) -> list[Finding]:
    found = []
    for line in prose(lines):
        for match in ID_TOKEN.finditer(expand_ranges(line.text)):
            token = match.group(0)
            if unreal_id(token):
                continue
            if not re.fullmatch(ID_FORMATS[match.group(1)], token):
                found.append(Finding("B9", name, line.number, token))
    return found


def check_b13(name: str, lines: list[Line]) -> list[Finding]:
    if not RETIRED_PREFIXES:
        return []
    return [Finding("B13", name, line.number, quote(line.text)) for line in prose(lines) if RETIRED_ID.search(line.text)]


LINE_CHECKS = {
    "A1": lambda n, ls: scan_pattern("A1", n, ls, A1_PATTERN),
    "A2": check_a2,
    "A3": check_a3,
    "A4": check_a4,
    "A5": lambda n, ls: scan_pattern("A5", n, ls, A5_PATTERN),
    "B2": check_b2, "B3": check_b3, "B4": check_b4, "B5": check_b5, "B6": check_b6, "B9": check_b9,
    "B13": check_b13,
}


ANY_ID = r"\b(?:UC|FR|NFR|BR|ERR|AC|G)-[A-Z0-9][A-Z0-9.-]*[A-Z0-9]\b"


def unreal_id(token: str) -> bool:
    return bool(PLACEHOLDER.search(token) or GROUP_PREFIX.fullmatch(token))


def definition_ids(name: str, lines: list[Line]) -> dict[str, set[int]]:
    defined: dict[str, set[int]] = defaultdict(set)
    for line in prose(lines):
        heading = HEADING.match(line.text)
        if heading:
            candidates = re.findall(ANY_ID, heading.group(2))
        elif TABLE_ROW.match(line.text):
            first = split_cells(line.text)[0].strip().strip("*`").strip()
            candidates = [first] if re.fullmatch(ANY_ID, first) else []
        else:
            candidates = []
        for ident in candidates:
            if not unreal_id(ident):
                defined[ident].add(line.number)
    return defined


def reference_ids(lines: list[Line], skip: dict[str, set[int]]) -> dict[str, list[int]]:
    refs: dict[str, list[int]] = defaultdict(list)
    for line in prose(lines):
        for token in re.finditer(ANY_ID, expand_ranges(line.text)):
            ident = token.group(0)
            if not unreal_id(ident) and line.number not in skip.get(ident, set()):
                refs[ident].append(line.number)
    return refs


def check_c1(corpus: dict[str, list[Line]]) -> list[Finding]:
    defined: set[str] = set()
    referenced: dict[str, tuple[str, int]] = {}
    for name, lines in corpus.items():
        sites = definition_ids(name, lines)
        defined |= set(sites)
        for ident, numbers in reference_ids(lines, sites).items():
            referenced.setdefault(ident, (name, numbers[0]))
    groups = {re.sub(r"\.\d+$", "", ident) for ident in defined if ident.startswith("AC-")}
    undefined = [Finding("C1", n, ln, f"referenced, never defined: {i}") for i, (n, ln) in sorted(referenced.items()) if i not in defined | groups]
    unused = [Finding("C1", "-", 0, f"defined, never referenced elsewhere: {i}") for i in sorted(defined) if not re.fullmatch(r"AC-.*\.\d+", i) and i not in referenced and re.sub(r"\.\d+$", "", i) not in referenced]
    return undefined + unused


GLOBAL_CLAIMS = [
    (r"(?<![\d.])(\d+)\s+ca sử dụng(?! chi tiết theo| trong)", "UC", 16),
    (r"(?<![\d.])(\d+)\s+yêu cầu chức năng", "FR", 0),
    (r"(?<![\d.])(\d+)\s+yêu cầu phi chức năng", "NFR", 0),
    (r"(?<![\d.])(\d+)\s+quy tắc nghiệp vụ", "BR", 0),
    (r"(?<![\d.])(\d+)\s+mục tiêu hệ thống", "G", 0),
]


def distinct_ids(corpus: dict[str, list[Line]], prefix: str) -> int:
    pattern = re.compile(r"\b%s-(?:[A-Z]{3}-)?\d{1,3}\b" % prefix)
    found = {m.group(0) for lines in corpus.values() for line in prose(lines) for m in pattern.finditer(expand_ranges(line.text))}
    return len(found)


def check_c3(corpus: dict[str, list[Line]]) -> list[Finding]:
    found = []
    for pattern, prefix, floor in GLOBAL_CLAIMS:
        actual = distinct_ids(corpus, prefix)
        for name, lines in corpus.items():
            for line in prose(lines):
                for match in re.finditer(pattern, line.text.replace("*", ""), re.IGNORECASE):
                    claimed = int(match.group(1))
                    if claimed >= floor and claimed != actual:
                        found.append(Finding("C3", name, line.number, f"claims {claimed}, counted {actual}: {quote(match.group(0))}"))
    return found


def normalize_heading(text: str) -> str:
    return re.sub(r"\s+", " ", text.strip()).casefold()


def toc_entries(lines: list[Line]) -> list[Line]:
    return [line for line in lines if re.match(r"^\s*- \[", line.text)]


def collect_headings(corpus: dict[str, list[Line]]) -> tuple[set[str], dict[str, tuple[str, int, int]]]:
    every: set[str] = set()
    lowest: dict[str, tuple[str, int, int]] = {}
    for name, lines in corpus.items():
        for line in prose(lines) if name != TOC_FILE else []:
            match = HEADING.match(line.text)
            if not match or not 2 <= len(match.group(1)) <= 4:
                continue
            key, level = normalize_heading(match.group(2)), len(match.group(1))
            every.add(key)
            if key not in lowest or level < lowest[key][2]:
                lowest[key] = (name, line.number, level)
    return every, lowest


def check_c4(corpus: dict[str, list[Line]]) -> list[Finding]:
    toc_lines = corpus.get(TOC_FILE)
    if toc_lines is None:
        return [Finding("C4", TOC_FILE, 0, "table of contents file not found")]
    every, lowest = collect_headings(corpus)
    found, seen = [], set()
    for entry in toc_entries(toc_lines):
        label = re.match(r"^\s*- \[(.*?)\]\(", entry.text)
        key = normalize_heading(label.group(1)) if label else ""
        seen.add(key)
        if key not in every:
            found.append(Finding("C4", TOC_FILE, entry.number, f"no matching heading: {quote(entry.text, 80)}"))
    for key, (name, number, level) in lowest.items():
        if key not in seen and level <= 3:
            found.append(Finding("C4", name, number, f"heading missing from table of contents: {quote(key, 80)}"))
    return found


def citation_entries(lines: list[Line]) -> list[Line]:
    return [line for line in lines if line.text.startswith("- ") and re.search(r"\(\d{4}[a-z]?[,)]", line.text)]


def parse_entry(text: str) -> tuple[str, str, list[str]]:
    surname = re.match(r"^- ([^,.(]+)", text)
    year = re.search(r"\((\d{4}[a-z]?)", text)
    standards = [s.split(";")[-1].strip() for s in re.findall(r"\(((?:RFC|ISO|IEC|IEEE|ASTM)[^)]*)\)", text)]
    return (surname.group(1).strip() if surname else "", year.group(1) if year else "", standards)


NAME = r"[A-Z][A-Za-z\-]+(?: (?:[A-Z][A-Za-z\-]+|van|von|der|de|del|bin))*"
PART_MARKER = re.compile(r"^(?:xem |see )?(" + NAME + r")(?: et al\.| (?:&|và) " + NAME + r")?,\s*((?:19|20)\d{2}[a-z]?)")
NARRATIVE_MARKER = re.compile(r"\b(" + NAME + r")(?: et al\.)? \(((?:19|20)\d{2}[a-z]?)[,)]")


def body_markers(text: str, parenthetical_only: bool = False) -> list[tuple[str, str]]:
    pairs = [] if parenthetical_only else [m.groups() for m in NARRATIVE_MARKER.finditer(text)]
    for group in re.findall(r"\(([^()]*(?:19|20)\d{2}[a-z]?[^()]*)\)", text):
        pairs.extend(m.groups() for part in group.split(";") if (m := PART_MARKER.match(part.strip())))
    return pairs


def check_b10(corpus: dict[str, list[Line]]) -> list[Finding]:
    """Enforce standalone bibliography: verify File 11 presence and zero residual in-text markers in body."""
    citation = corpus.get(CITATION_FILE)
    if citation is None:
        return [Finding("B10", CITATION_FILE, 0, "citation file not found")]
    body = [(n, l.number, l.text) for n, ls in corpus.items() if n != CITATION_FILE for l in prose(ls)]
    found = []
    for name, number, text in body:
        for author, year in body_markers(text, parenthetical_only=False):
            if re.fullmatch(r"Q[0-9]", author):
                continue
            found.append(Finding("B10", name, number, f"residual in-text citation marker found: {author}, {year}"))
    return found


def load_corpus(folder: Path) -> dict[str, list[Line]]:
    return {path.name: load_lines(path) for path in sorted(folder.glob("[0-9][0-9]-*.md"))}


def run_checks(corpus: dict[str, list[Line]], selected: list[str]) -> list[Finding]:
    findings: list[Finding] = []
    for name, lines in corpus.items():
        if "B2" in selected:
            findings.extend(unclosed_fence(name, lines))
        for check_id, check in LINE_CHECKS.items():
            if check_id in selected:
                findings.extend(check(name, lines))
    cross = {"C1": check_c1, "C3": check_c3, "C4": check_c4, "B10": check_b10}
    for check_id, check in cross.items():
        if check_id in selected:
            findings.extend(check(corpus))
    return findings


def report(findings: list[Finding], selected: list[str], details: bool) -> None:
    for finding in findings if details else []:
        print(f"{finding.check}\t{finding.file}:{finding.line}\t{finding.text}")
    counts = defaultdict(int)
    for finding in findings:
        counts[finding.check] += 1
    print("check\tcount")
    for check_id in CHECK_IDS:
        if check_id in selected:
            print(f"{check_id}\t{counts[check_id]}")
    print(f"TOTAL\t{len(findings)}")


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(prog="srs_lint", description=__doc__)
    parser.add_argument("folder", nargs="?", default=str(DEFAULT_DIR))
    parser.add_argument("--details", action="store_true", help="print every finding")
    parser.add_argument("--only", help="comma-separated check ids, for example A1,B2")
    args = parser.parse_args(argv)
    folder = Path(args.folder)
    if not folder.is_dir():
        print(f"srs_lint: not a folder: {folder}", file=sys.stderr)
        return 2
    selected = args.only.upper().split(",") if args.only else CHECK_IDS
    findings = run_checks(load_corpus(folder), selected)
    report(findings, selected, args.details)
    return 1 if findings else 0


if __name__ == "__main__":
    raise SystemExit(main())
