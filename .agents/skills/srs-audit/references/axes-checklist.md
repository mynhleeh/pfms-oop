# Axes Checklist

Every check yields a count. The pass threshold is 0 unless the table says otherwise. "Script" means `srs_lint.py` covers it; "Reviewer" means a fresh-context subagent must judge it.

## Axis A: Wording and AI tells

| # | Check | Covered by |
| --- | --- | --- |
| A1 | Em dash, en dash or `--` used as a sentence break outside code fences | Script |
| A2 | Vietnamese term followed by a parenthesized English term, such as `Yêu cầu (Requirement)` | Script |
| A3 | Bullet with a bold prefix, such as `- **Title:** text` | Script |
| A4 | False contrast ("không chỉ X mà còn Y") and forced rhythmic triplets | Script for the contrast, Reviewer for triplets |
| A5 | Forbidden vocabulary of rule 02 section 2.5 | Script |
| A6 | A sentence containing one of: nhanh, chậm, hiệu quả, thân thiện, phù hợp, linh hoạt, dễ dàng, ổn định, an toàn, đầy đủ, with no number, unit or named criterion in the same sentence | Reviewer, count by sentence |
| A7 | A section that ends with a one-sentence paragraph restating the section, a section that opens with a sentence announcing what it will do, or praise words with no measure | Reviewer, with quote |
| A8 | One concept called by several names. Build a term list first: one row per domain noun with every variant found and its `file:line`; a row with 2 or more variants not defined as synonyms is a finding | Reviewer |

## Axis B: Technical standard

| # | Check | Covered by |
| --- | --- | --- |
| B1 | Design leakage into the SRS: class names, GoF pattern tags, database schema, method names, Java exception names | Reviewer |
| B2 | Table column parity between header, delimiter and every data row (also reports an unclosed code fence) | Script |
| B3 | Unescaped `\|` inside code or math within a table cell | Script |
| B4 | Empty cell written as an em dash instead of one space | Script |
| B5 | More than 2 consecutive tables in one H2 or H3 without a lead-in | Script |
| B6 | Prose paragraph over 8 consecutive lines | Script |
| B7 | Paragraph that repeats an adjacent table or diagram in full | Reviewer |
| B8 | Wrong presentation form against the table in rule 02 section 4.1 | Reviewer |
| B9 | Truncated or malformed traceability identifier | Script |
| B10 | Citation marker without an entry, or entry without a marker | Script |
| B11 | Diagram defects: missing actor, wrong notation, label not matching a UC code | Reviewer |
| B12 | Requirement without a verifiable criterion, NFR without a measure | Reviewer |
| B13 | Identifier that still uses a retired prefix (for example `TC-`, replaced by `AC-`) | Script |

## Axis C: Consistency across files

| # | Check | Covered by |
| --- | --- | --- |
| C1 | Identifier referenced but not defined, or defined but never referenced (`AC-*.x` leaf codes are exempt) | Script |
| C2 | Traceability matrix against the functional requirements, use cases and business rules: missing, extra or wrong rows | Reviewer |
| C3 | Count claims ("21 ca sử dụng", "22 yêu cầu chức năng") against real counts | Script for global claims, Reviewer for the rest |
| C4 | Table of contents against real headings | Script |
| C5 | Same value (rate, threshold, formula, rounding) stated differently in two places | Reviewer. Build a number list first: one row per rate, threshold, formula, date and rounding rule with every `file:line`; a row whose values differ is a finding |
| C6 | Business rules against functional requirements and use-case flows: conditions, states, exceptions | Reviewer |
| C7 | Diagrams against use-case specifications: actors, include/extend, names | Reviewer |
| C8 | Introduction, system description and summary against the body | Reviewer |
| C9 | Rounding mode and money type against rule 04 section 1 (`HALF_EVEN` unless the SRS documents an exception with a `BR-*` code) | Reviewer |
| C10 | Wording or rule contrary to the assignment requirement | Reviewer |

A contradiction between two peer files that the SRS cannot settle is a decision, not a formatting defect. Stop and ask the user (rule 00 R2), one question per message, with: the nature of the issue, an example with dates and numbers, an options table, and a recommendation with its source.

## Finding report format

Each finding carries: location `file:line`, verbatim quote, occurrence count, violated rule, fix. Reject any finding that lacks a quote or a count.
