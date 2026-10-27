"""Measure whitespace defects (W1-W7) and baseline invariants across SRS files."""

import hashlib
import os
import re
import sys
import unicodedata
from collections import Counter
from dataclasses import dataclass
from pathlib import Path

DEFAULT_SRS = Path(__file__).resolve().parent.parent / "docs" / "software-requirement-specification"
SRS_DIR = DEFAULT_SRS if DEFAULT_SRS.exists() else Path(r"C:\Personal Files\repos\fin_mng-prj_mng\main\docs\software-requirement-specification")
EXCLUDE_PREFIX = "11-"

@dataclass
class Finding:
    category: str
    line: int
    col: int
    detail: str

def parse_code_fences(lines: list[str]) -> list[bool]:
    """Return boolean array indicating if line is inside a code fence."""
    in_fence = False
    fence_marker = ""
    in_fences = []
    for line in lines:
        m = re.match(r"^ {0,3}(`{3,}|~{3,})", line)
        if in_fence:
            in_fences.append(True)
            if m and m.group(1)[0] == fence_marker[0] and len(m.group(1)) >= len(fence_marker):
                in_fence = False
        elif m:
            in_fence = True
            fence_marker = m.group(1)
            in_fences.append(True)
        else:
            in_fences.append(False)
    return in_fences

def split_table_cells(line: str) -> list[str]:
    """Split markdown table row by unescaped pipe."""
    stripped = line.strip()
    if not stripped.startswith("|") or not stripped.endswith("|") or len(stripped) < 2:
        return []
    cells = re.split(r"(?<!\\)\|", stripped)[1:-1]
    return cells

def is_delimiter_row(cells: list[str]) -> bool:
    if not cells:
        return False
    return all(re.match(r"^\s*:?-{1,}:?\s*$", c) for c in cells)

def analyze_file(path: Path) -> dict:
    raw_bytes = path.read_bytes()
    text = raw_bytes.decode("utf-8-sig")
    lines = text.split("\n")
    
    # Invariant baselines
    no_space = "".join(ch for ch in text if not ch.isspace())
    content_fingerprint = hashlib.sha256(no_space.encode("utf-8")).hexdigest()
    
    # Traceability codes
    codes = re.findall(r"\b(BR|UC|FR|NFR|CON|ERR)-[A-Za-z0-9_.-]+", text)
    code_counts = Counter(codes)
    code_cat_counts = Counter(c.split("-")[0] for c in codes)
    
    # Table lines: count lines starting with optional whitespace then |
    table_line_indices = [i for i, l in enumerate(lines) if l.strip().startswith("|")]
    total_table_lines = len(table_line_indices)
    
    in_fences = parse_code_fences(lines)
    
    # Parse tables
    tables = []
    curr_table = []
    for i, line in enumerate(lines):
        if not in_fences[i] and line.strip().startswith("|") and line.strip().endswith("|") and len(line.strip()) > 1:
            curr_table.append((i, line))
        else:
            if curr_table:
                tables.append(curr_table)
                curr_table = []
    if curr_table:
        tables.append(curr_table)
        
    table_structures = []
    for t_idx, t in enumerate(tables, 1):
        header_cells = split_table_cells(t[0][1])
        num_cols = len(header_cells)
        num_rows = len(t)
        table_structures.append({
            "table_id": t_idx,
            "start_line": t[0][0] + 1,
            "end_line": t[-1][0] + 1,
            "rows": num_rows,
            "cols": num_cols,
        })
        
    # Whitespace defects
    findings: list[Finding] = []
    
    # W1 and W2 in tables
    for t in tables:
        for row_pos, (line_idx, line_str) in enumerate(t):
            cells = split_table_cells(line_str)
            if not cells:
                continue
            if is_delimiter_row(cells):
                for c_idx, c in enumerate(cells):
                    if c not in (" --- ", " :--- ", " :---: ", " ---: "):
                        findings.append(Finding(
                            "W2", line_idx + 1, c_idx + 1,
                            f"Elongated or non-standard delimiter cell: {c!r}"
                        ))
            else:
                for c_idx, c in enumerate(cells):
                    if c.strip() == "":
                        if len(c) > 2:
                            findings.append(Finding(
                                "W1", line_idx + 1, c_idx + 1,
                                f"Empty cell with excess padding ({len(c)} spaces): {c!r}"
                            ))
                    else:
                        if c.startswith("  ") or c.endswith("  "):
                            findings.append(Finding(
                                "W1", line_idx + 1, c_idx + 1,
                                f"Cell padding: {c[:25]!r} (lead={len(c)-len(c.lstrip())}, trail={len(c)-len(c.rstrip())})"
                            ))
                            
    # W3: Trailing whitespace
    for i, line in enumerate(lines):
        if in_fences[i]:
            continue
        m = re.search(r"([ \t]+)$", line)
        if m:
            trailing = m.group(1)
            is_intentional = False
            if trailing == "  " and i + 1 < len(lines):
                next_l = lines[i+1].strip()
                if (next_l and not next_l.startswith("#") and not next_l.startswith("|") 
                    and not next_l.startswith("-") and not next_l.startswith("*") 
                    and not next_l.startswith(">") and not re.match(r"^\d+[.)]", next_l)
                    and not next_l.startswith("```")):
                    is_intentional = True
            if not is_intentional:
                findings.append(Finding(
                    "W3", i + 1, len(line) - len(trailing) + 1,
                    f"Trailing whitespace ({len(trailing)} chars): {trailing!r}"
                ))

    # W4: 2+ spaces between words in prose, list items, table cells
    for i, line in enumerate(lines):
        if in_fences[i]:
            continue
        cleaned = re.sub(r"\$\$[^\$]+\$\$", "M", line)
        cleaned = re.sub(r"\$[^\$\n]+\$", "M", cleaned)
        cleaned = re.sub(r"`[^`]+`", "C", cleaned)
        cleaned = re.sub(r"<[^>]+>", "H", cleaned)
        
        if line.strip().startswith("|") and line.strip().endswith("|") and len(line.strip()) > 1:
            cells = split_table_cells(cleaned)
            if not is_delimiter_row(cells):
                for c_idx, c in enumerate(cells):
                    inner = c.strip()
                    matches = list(re.finditer(r"\S(  +)\S", inner))
                    for m in matches:
                        findings.append(Finding(
                            "W4", i + 1, m.start() + 1,
                            f"Multiple spaces between words in table cell: {inner[max(0, m.start()-5):m.end()+5]!r}"
                        ))
        else:
            m_list = re.match(r"^(\s*(?:[-*+]|\d+[.)]))(\s+)(.*)$", cleaned)
            if m_list:
                marker = m_list.group(1)
                space_after_marker = m_list.group(2)
                item_content = m_list.group(3)
                if len(space_after_marker) > 1:
                    findings.append(Finding(
                        "W4", i + 1, len(marker) + 1,
                        f"Multiple spaces after list marker ({len(space_after_marker)} spaces): {space_after_marker!r}"
                    ))
                for m in re.finditer(r"\S(  +)\S", item_content):
                    findings.append(Finding(
                        "W4", i + 1, len(marker) + len(space_after_marker) + m.start() + 1,
                        f"Multiple spaces in list item: {item_content[max(0, m.start()-5):m.end()+5]!r}"
                    ))
            else:
                prose_text = cleaned.strip()
                if prose_text:
                    for m in re.finditer(r"\S(  +)\S", prose_text):
                        findings.append(Finding(
                            "W4", i + 1, m.start() + 1,
                            f"Multiple spaces in prose: {prose_text[max(0, m.start()-5):m.end()+5]!r}"
                        ))

    # W5: Consecutive blank lines >= 2
    blank_run = 0
    for i, line in enumerate(lines):
        if in_fences[i]:
            blank_run = 0
            continue
        if line.strip() == "":
            blank_run += 1
            if blank_run >= 2:
                findings.append(Finding(
                    "W5", i + 1, 1,
                    f"Consecutive blank line #{blank_run}"
                ))
        else:
            blank_run = 0

    # W6: Tabs or irregular list indentation (rule 08 section 2.4: nested item aligns to parent text column)
    text_columns = []
    for i, line in enumerate(lines):
        if in_fences[i]:
            continue
        if "\t" in line:
            findings.append(Finding(
                "W6", i + 1, line.find("\t") + 1,
                "Tab character used for indentation or spacing"
            ))
        m = re.match(r"^( *)([-*+]|\d+[.)]) ", line)
        if m:
            indent = len(m.group(1))
            text_columns = [col for col in text_columns if col <= indent]
            if indent > 0 and indent not in text_columns:
                findings.append(Finding(
                    "W6", i + 1, 1,
                    f"Irregular list indentation: {indent} spaces (not the text column of a parent list marker)"
                ))
            text_columns.append(indent + len(m.group(2)) + 1)
        elif line.startswith("#") or (
            line.strip() and not line.startswith(" ") and i > 0 and lines[i - 1].strip() == ""
        ):
            text_columns = []

    # W7: Missing blank lines around headings, tables, code blocks; EOF newline
    for i, line in enumerate(lines):
        if in_fences[i]:
            continue
        if line.startswith("#"):
            if i > 0 and lines[i-1].strip() != "":
                findings.append(Finding(
                    "W7", i + 1, 1,
                    f"Heading missing blank line before: {line[:30]!r}"
                ))
            if i + 1 < len(lines) and lines[i+1].strip() != "":
                findings.append(Finding(
                    "W7", i + 1, 1,
                    f"Heading missing blank line after: {line[:30]!r}"
                ))

    is_tbl_row = [
        (not in_fences[idx] and l.strip().startswith("|") and l.strip().endswith("|") and len(l.strip()) > 1)
        for idx, l in enumerate(lines)
    ]
    in_tbl = False
    for i, is_t in enumerate(is_tbl_row):
        if is_t and not in_tbl:
            in_tbl = True
            if i > 0 and lines[i-1].strip() != "":
                findings.append(Finding(
                    "W7", i + 1, 1,
                    f"Table missing blank line before (table start at L{i+1})"
                ))
        elif not is_t and in_tbl:
            in_tbl = False
            if lines[i].strip() != "":
                findings.append(Finding(
                    "W7", i, 1,
                    f"Table missing blank line after (table ended at L{i})"
                ))

    for i, line in enumerate(lines):
        m = re.match(r"^ {0,3}(`{3,}|~{3,})", line)
        if m:
            if i == 0 or not in_fences[i-1]:
                if i > 0 and lines[i-1].strip() != "":
                    findings.append(Finding(
                        "W7", i + 1, 1,
                        f"Code fence missing blank line before at L{i+1}"
                    ))
            if i == len(lines) - 1 or not in_fences[i+1]:
                if i + 1 < len(lines) and lines[i+1].strip() != "":
                    findings.append(Finding(
                        "W7", i + 1, 1,
                        f"Code fence missing blank line after at L{i+1}"
                    ))

    if not text.endswith("\n"):
        findings.append(Finding(
            "W7", len(lines), 1,
            "Missing trailing newline at EOF"
        ))
    elif text.endswith("\n\n"):
        trailing_blanks = 0
        idx = len(lines) - 1
        while idx >= 0 and lines[idx] == "":
            trailing_blanks += 1
            idx -= 1
        if trailing_blanks > 1:
            findings.append(Finding(
                "W7", len(lines), 1,
                f"Excess trailing newlines at EOF ({trailing_blanks} blank lines)"
            ))

    counts = Counter(f.category for f in findings)
    
    return {
        "file": path.name,
        "total_lines": len(lines),
        "fingerprint": content_fingerprint,
        "table_lines": total_table_lines,
        "tables": table_structures,
        "code_counts": code_counts,
        "code_cat_counts": code_cat_counts,
        "findings": findings,
        "counts": {f"W{k}": counts.get(f"W{k}", 0) for k in range(1, 8)},
    }

if __name__ == "__main__":
    files = sorted(SRS_DIR.glob("*.md"))
    results = []
    for p in files:
        if p.name.startswith(EXCLUDE_PREFIX):
            continue
        res = analyze_file(p)
        results.append(res)
        c = res["counts"]
        print(f"{res['file']:45s} | W1={c['W1']:4d} | W2={c['W2']:3d} | W3={c['W3']:3d} | W4={c['W4']:3d} | W5={c['W5']:3d} | W6={c['W6']:3d} | W7={c['W7']:3d}")
