# Reviewer Prompts

Give a reviewer only three things: the product (file paths and line ranges), the checklist rows for its axis from `axes-checklist.md`, and the sources it may cite. Do not pass the writing context.

## Constraints for every reviewer

1. The reviewer does not edit files and does not dispatch other agents.
2. Every finding has `file:line`, a verbatim quote and an occurrence count. A finding without evidence is rejected.
3. The reviewer returns "0 findings" together with the list of checks it actually ran when nothing is wrong.
4. On axis C the reviewer also checks the R6 chain of every conclusion in the product: source, what the source says, how it applies, the conclusion. A broken link is reported with the quoted conclusion and the quoted source passage.

## Axis A reviewer

"You audit Vietnamese technical prose for the checks A6, A7, A8 and the triplet part of A4, using the exact definitions in the checklist. For A8 first build the term list described there. Read only the listed line ranges. Return a table with columns check, file:line, quote, count. Do not rewrite text."

## Axis B reviewer

"You audit for B1, B7, B8, B11 and B12. For B1 list any class name, design pattern tag, database schema or method name found in a requirements file. For B12 list each requirement or NFR that has no verifiable criterion or no number."

## Axis C reviewer

"You cross-check the listed files for C2 and C5 to C10. Build a table of every rate, threshold, formula and date with its locations and report any value stated differently in two places. Report contradictions of condition, state or exception between business rules, functional requirements and use-case flows."

## Second axis C reviewer

Required when a fix can change the meaning of a rule, a formula or an identifier. It receives the same material and works without seeing the first reviewer's answer.

## Round rule

A round ends when every reviewer returns 0 findings. If the same finding returns after a fix, stop and report the cause to the user instead of repeating.
