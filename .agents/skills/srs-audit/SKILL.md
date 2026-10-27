---
name: srs-audit
description: "Use when auditing or quality-checking a Vietnamese SRS (requirements specification) set in this workspace: finding wording defects and AI tells, technical-standard violations, or cross-file contradictions in the numbered SRS markdown files, or when asked to run, extend or interpret the SRS lint script. Trigger on 'rà soát SRS', 'kiểm toán SRS', 'audit SRS', 'lint SRS', 'SRS có mâu thuẫn không', 'đếm lỗi SRS'. Do NOT trigger for writing new requirements, OOA/OOD diagrams or Java code."
version: 1.0.0
tags:
  - srs
  - audit
  - lint
  - requirements
  - documentation-quality
  - traceability
---

# SRS Audit

## When to Activate

Activate when a request asks to find, count or fix defects in the numbered SRS markdown files of a branch (default location: `<branch>/docs/software-requirement-specification/`, files `00` to `11`), or to run or change the lint script that counts those defects.

Do not activate for drafting new requirements, which belongs to the SPECIFY flow of rule 06.

## Overview

The audit has three axes. Each check has a countable pass threshold, so a claim of "clean" is always a number.

| Axis | Subject | Checks | Mostly automated |
| --- | --- | --- | --- |
| A | Wording and AI tells | A1 to A8 | A1 to A5 |
| B | Technical standard | B1 to B13 | B2 to B6, B9, B10, B13 |
| C | Cross-file consistency | C1 to C10 | C1, C3, C4 |

The full table with thresholds is in `references/axes-checklist.md`. Checks that are not automated need a reviewer with fresh context.

## Instructions

1. Resolve the branch and list the SRS folder. Do not trust a remembered path (rule 06 section 7).
2. Locate the lint script by listing `<branch>/tools/`. If `srs_lint.py` is missing, say so and stop; do not rewrite it from memory.
3. Run the script and keep the real output as the baseline:
   `python -I <branch>/tools/srs_lint.py` for counts, add `--details` for `check<TAB>file:line<TAB>quote` rows, `--only A1,B2` to select checks.
4. Exit code 0 means no finding, 1 means at least one finding, 2 means the folder was not found. Check IDs given to `--only` are case-insensitive, and an unknown ID is ignored without an error, so trust only the rows printed in the count table. On a folder without `01-muc-luc-tong-quan.md` or `11-tai-lieu-trich-dan.md`, C4 and B10 each report 1 for the missing file.
5. Treat the count as a floor. Run the manual checks in `references/axes-checklist.md` with fresh reviewers (one per axis). Add a second independent axis C reviewer when a fix changes a number, formula, threshold, rounding mode or identifier.
6. Report each finding as `file:line`, verbatim quote, occurrence count, violated rule, proposed fix. A finding without a quote is rejected.
7. Classify each fix. Formatting, table structure and cross-references are Low risk. A change to a business rule, formula, threshold or identifier is High risk and needs a user decision before it is written (rule 00 R2, R6).
8. After fixing, rerun the script and report before and after counts per check.

## Output Contract

One findings table per unit, then one before and after count table. Never write "aligned", "clean" or "error-free" without the counts that prove it (rule 00 R3).

Finding table columns: `#`, `check`, `file:line`, `quote`, `count`, `rule`, `fix`.

## Anti-Examples

| Avoid | Prefer | Why |
| --- | --- | --- |
| "SRS looks consistent" | "C1 = 0, C3 = 0, C4 = 0 from the run on <date>" | A claim without counts is unverified. |
| Fixing a threshold that two files state differently | Ask the user which value is right, then write it | Which value is correct is a decision, not a typo. |
| Letting the writing agent review its own fix | A fresh reviewer that sees only product, checklist and sources | A reviewer that wrote the text repeats its blind spots. |
| Treating lint 0 as proof for A6 to A8, B1, B7, B8, B11, B12, C2, C5 to C10 | Reviewer pass on those checks | The script cannot judge meaning. |

## Common Issues

| Symptom | Cause | Fix |
| --- | --- | --- |
| A check reports 0 on a file with a visible defect | The script skips fenced code and only knows specific forms | See `references/lint-limitations.md`, then do the check by hand. |
| B10 reports every citation entry as unused | The body cites sources in a form the script does not know, or not at all | Read the body, confirm, then ask the user whether to add markers or drop entries (rule 00 R2, rule 07 section 4.1). Never invent a missing bibliographic field (rule 07 section 3.7). |
| C1 reports an `ERR-*` code as defined but never referenced | Real orphan, or referenced only in another form | Grep the code across the folder, then ask the user. |

## References

| File | Read when |
| --- | --- |
| `references/axes-checklist.md` | Building the findings table or briefing a reviewer |
| `references/reviewer-prompts.md` | Dispatching a reviewer subagent |
| `references/lint-limitations.md` | A lint count looks too low or too high |
