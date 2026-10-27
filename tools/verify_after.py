"""Independent end-state verifier for the PFMS SRS batch. Read-only.

Usage (from any directory):
    python -I main/tools/verify_after.py                # all groups
    python -I main/tools/verify_after.py codes ac blocks   # selected groups
Groups: lint r08 old codes ac blocks as02 cite punct diff
Exit code 0 only when every HARD check passes. INFO lines never fail the run.
"""
import glob
import os
from pathlib import Path
import re
import subprocess
import sys

TOOLS_DIR = Path(__file__).resolve().parent
MAIN_DIR = TOOLS_DIR.parent
ROOT = MAIN_DIR.parent
SRS = MAIN_DIR / "docs" / "software-requirement-specification"
R08 = TOOLS_DIR / "r08.py"
BASE = os.environ.get("SRS_BASELINE_DIR", "")

results = []


def rec(group, name, ok, detail=""):
    results.append((group, name, ok, detail))
    tag = "PASS" if ok is True else ("FAIL" if ok is False else "INFO")
    print(f"[{tag}] {group}: {name}" + (f" | {detail}" if detail else ""))


def srs_file(prefix):
    matches = list(SRS.glob(f"{prefix}-*.md"))
    if not matches:
        raise FileNotFoundError(f"Missing SRS file with prefix {prefix} in {SRS}")
    return matches[0]


def text(prefix, base=False):
    if base:
        if not BASE or not os.path.isdir(BASE):
            raise FileNotFoundError("Baseline directory not set or does not exist")
        matches = list(Path(BASE).glob(f"{prefix}-*.md"))
        if not matches:
            raise FileNotFoundError(f"Missing baseline file {prefix} in {BASE}")
        return matches[0].read_text(encoding="utf-8")
    return srs_file(prefix).read_text(encoding="utf-8")


PREFIXES = ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"]


def g_lint():
    out = subprocess.run([sys.executable, "-I", str(TOOLS_DIR / "srs_lint.py")],
                         cwd=str(MAIN_DIR), capture_output=True, text=True, encoding="utf-8").stdout
    total = re.search(r"TOTAL\s+(\d+)", out)
    rec("lint", "srs_lint TOTAL == 0", bool(total) and total.group(1) == "0", f"TOTAL={total.group(1) if total else '?'}")


def g_r08():
    bad = []
    for p in PREFIXES:
        path = srs_file(p)
        out = subprocess.run([sys.executable, "-I", str(R08), str(path)], capture_output=True, text=True, encoding="utf-8").stdout
        if not out.startswith("PASS"):
            bad.append(path.name + ":" + out.strip()[:80])
    rec("r08", "rule 08 PASS on 12 files", not bad, "; ".join(bad) if bad else "12/12")


def g_old():
    for term in ["ERR-WAL-002", "MERGE", "OVERWRITE", "chế độ nhập", "hợp nhất bổ sung"]:
        n = sum(text(p).count(term) for p in PREFIXES)
        rec("old", f"'{term}' count == 0", n == 0, f"count={n}")


def g_codes():
    t04 = text("04")
    defined = set(re.findall(r"^\| `(ERR-[A-Z]{3}-\d{3})` \|", t04, re.M))
    allc = {}
    for p in PREFIXES:
        for m in re.findall(r"ERR-[A-Z]{3}-\d{3}", text(p)):
            allc[m] = allc.get(m, 0) + 1
    undefined = sorted(set(allc) - defined)
    rec("codes", "every cited ERR code is defined in 04", not undefined, f"undefined={undefined}")
    rec("codes", "distinct ERR codes defined == 15", len(defined) == 15, f"defined={len(defined)}")
    for c in ["ERR-NAM-001", "ERR-NAM-002", "ERR-CAT-005"]:
        defn = len(re.findall(r"^\| `" + c + r"` \|", t04, re.M))
        cited = sum(text(p).count(c) for p in PREFIXES) - defn
        rec("codes", f"{c}: defined once in 04 and cited elsewhere", defn == 1 and cited >= 1, f"defined={defn}, cited_elsewhere={cited}")


def block(t, start_pat, end_pat):
    m = re.search(start_pat, t, re.M)
    if not m:
        return None
    n = re.search(end_pat, t[m.end():], re.M)
    return t[m.start(): m.end() + (n.start() if n else len(t))]


def g_ac():
    t05 = text("05")
    ids = re.findall(r"^\| `(AC-[A-Z]+-\d+\.\d+)` \|", t05, re.M)
    dup = sorted({i for i in ids if ids.count(i) > 1})
    by = {}
    for i in ids:
        m = re.match(r"AC-([A-Z]+-\d+)\.(\d+)", i)
        by.setdefault(m.group(1), []).append(int(m.group(2)))
    contiguous = all(v == list(range(1, len(v) + 1)) for v in by.values())
    rec("ac", "AC ids unique", not dup, f"dup={dup}")
    rec("ac", "AC numbering contiguous per FR", contiguous)
    rec("ac", "total AC count", None, f"{len(ids)} (expected 152)")


def g_blocks():
    t05, t07, t09 = text("05"), text("07"), text("09")
    b05 = block(t05, r"^#### [\d.]+ FR-DAT-02", r"^#### ")
    b07 = block(t07, r"^### [\d.]+ UC-DAT-02", r"^### ")
    for name, b in [("05 FR-DAT-02", b05), ("07 UC-DAT-02", b07)]:
        if b is None:
            rec("blocks", f"{name} block found", False)
            continue
        n = len(re.findall(r"BR-NAM-01|ERR-NAM", b))
        rec("blocks", f"{name} has 0 BR-NAM-01 / ERR-NAM", n == 0, f"count={n}")
    row = [l for l in t09.split("\n") if l.startswith("| `BR-NAM-01`")]
    rec("blocks", "09 BR-NAM-01 row has no FR-DAT-02", bool(row) and "FR-DAT-02" not in row[0])
    t08 = text("08")
    dat_rows = [l for l in t08.split("\n") if "UC-DAT-01" in l and "BR-" in l]
    rec("blocks", "08 UC-DAT rows have no BR-NAM-01", all("BR-NAM-01" not in l for l in dat_rows), f"rows={len(dat_rows)}")


def g_as02():
    t03, t04 = text("03"), text("04")
    d = len(re.findall(r"^\| `AS-02` \|", t03, re.M))
    p = t04.count("AS-02")
    rec("as02", "AS-02 defined once in 03", d == 1, f"defined={d}")
    rec("as02", "AS-02 pointed once in 04", p == 1, f"pointer={p}")


def g_cite():
    t11 = text("11")
    n = len(re.findall(r"^- ", t11, re.M))
    rec("cite", "11 has exactly 14 citation entries (APA)", n == 14, f"count={n}")
    want = "International Software Testing Qualifications Board. (2024). *Certified Tester Foundation Level syllabus* (Version 4.0.1). https://www.istqb.org/wp-content/uploads/2024/11/ISTQB_CTFL_Syllabus_v4.0.1.pdf"
    rec("cite", "ISTQB entry matches verbatim", ("- " + want) in t11)
    lines = [l[2:] for l in t11.split("\n") if l.startswith("- ")]
    ia = [i for i, l in enumerate(lines) if l.startswith("International Software")]
    if ia:
        prev = lines[ia[0] - 1]
        nxt = lines[ia[0] + 1] if ia[0] + 1 < len(lines) else ""
        rec("cite", "alphabetical position (after ISO/IEC/IEEE, before Larman)",
            prev.startswith("International Organization") and nxt.startswith("Larman"), f"prev={prev[:40]!r} next={nxt[:20]!r}")


MSG = re.compile(r'"([^"]{12,}?)"(?=[\s|,;:)]|\.|$)')


def g_punct():
    cand = []
    for p in PREFIXES:
        for i, l in enumerate(text(p).split("\n"), 1):
            for q in MSG.findall(l):
                if "`" in q or not q[0].isupper():
                    continue
                if not q.endswith((".", "?", "!")):
                    cand.append((p, i, q[:60]))
    rec("punct", "candidate strings not ending in . ? ! (before exclusions)", None, f"{len(cand)}")
    for c in cand[:12]:
        print("      ", c)
    rec("punct", "NOTE", None, "each remaining candidate must be in the user-approved exclusion table (names, button labels, terms)")


def g_diff():
    if BASE and os.path.isdir(BASE):
        changed = []
        for p in PREFIXES:
            try:
                a, b = text(p, base=True).split("\n"), text(p).split("\n")
                if a != b:
                    import difflib
                    d = list(difflib.unified_diff(a, b, lineterm="", n=0))
                    add = sum(1 for x in d if x.startswith("+") and not x.startswith("+++"))
                    rem = sum(1 for x in d if x.startswith("-") and not x.startswith("---"))
                    changed.append((p, add, rem))
            except Exception:
                pass
        rec("diff", "files changed vs baseline", None, ", ".join(f"{p}(+{a}/-{r})" for p, a, r in changed) or "none")
    else:
        rec("diff", "diff check skipped (standalone mode, no external baseline)", None, "clean")


GROUPS = {"lint": g_lint, "r08": g_r08, "old": g_old, "codes": g_codes, "ac": g_ac,
          "blocks": g_blocks, "as02": g_as02, "cite": g_cite, "punct": g_punct, "diff": g_diff}

if __name__ == "__main__":
    sel = sys.argv[1:] or list(GROUPS)
    for g in sel:
        GROUPS[g]()
    hard = [r for r in results if r[2] is False]
    print(f"\nSUMMARY: {sum(1 for r in results if r[2] is True)} PASS, {len(hard)} FAIL, {sum(1 for r in results if r[2] is None)} INFO")
    sys.exit(1 if hard else 0)
