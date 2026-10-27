# Lint Limitations

`srs_lint.py` is heuristic. A count of 0 means "none of the forms the script knows". These limits were found by independent review rounds and accepted.

| Check | Not detected or detected too widely |
| --- | --- |
| A2 | Parenthesized content containing `/` is skipped on purpose (for example `(float/double)`). Capitalized sentence-initial words are flagged only when the word carries a Vietnamese diacritic. |
| A3 | Bold written as `__text__` is not matched. |
| B2 | Tables without a leading `\|` are not parsed. |
| A1 | A table cell holding only an em dash is not counted, because B4 owns it. |
| B5 | Any non-blank non-table line, including an HTML comment, counts as a lead-in. Three tables separated only by comments are not flagged. |
| B6 | Blockquote lines count as paragraph lines. |
| B9 | Lowercase identifiers and identifiers using a non-breaking hyphen (U+2011) are not matched. |
| B13 | Lowercase identifiers and identifiers using a non-breaking hyphen (U+2011) are not matched. A line holding several retired codes counts once. |
| B10 | A parenthetical such as `(MVP, 2025)` can be taken for an author-year marker. A narrative form such as `Quý Ba (2025)` is ignored on the "marker without entry" side. Fences closed with an info string and ATX closing `#` marks are not normalized. |
| C1 | A definition written as a list item is not recognized. Every identifier in a heading counts as defined. Identifier ranges are expanded only up to 200 members and only in the forms `X-01..03`, `X-01..X-03` and `X-01–03`. |
| C3 | Unit-case claims below 16 are skipped. Subset phrases such as "5 yêu cầu chức năng của nhóm Ví" count as a mismatch of the global total. |

When a check seems too quiet, grep the folder for the defect form by hand and report both numbers.
