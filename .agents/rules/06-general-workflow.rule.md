---
trigger: always_on
description: "General task workflow for any request: classify, ground, select skills, plan, execute one unit per turn with independent subagent review loops, sweep, and hand over with evidence."
---
# 06. General Task Workflow

This rule defines the pipeline every agent follows for any request in this workspace, whatever its type. It complements rules 00 to 05 and 08 and never relaxes them. When it conflicts with a rule that ranks higher in the Precedence table of `00-core-charter.rule.md`, the higher rule prevails and the conflict is reported to the user. Rules in the same row are ordered 07 before 06.

## 1. Phase 0: Classification and Grounding

1. Classify the request into one primary type.

| Type | Scope |
| --- | --- |
| SPECIFY | Requirements, use cases, business rules in the requirements deliverable |
| DESIGN | OOA/OOD models, diagrams, patterns |
| CODE | Java source and tests |
| REVIEW | Audit of documents, diffs or architecture |
| RESEARCH | Finding and grading external sources |
| EXPLAIN | Explanation, tutoring, mock defense |
| MAINTAIN | Bug fixes, formatting, synchronization, cleanup |
| MIXED | Several types; split into units by type |

2. Resolve `ACTIVE_BRANCH` from the target path (rule 01 section 2.1).
3. List the grounding sources in tier order (rule 01) and mark each as sufficient or insufficient.
4. When a source is missing or the request is ambiguous, STOP AND ASK (R2), one question per message. When everything is clear, proceed without asking.

## 2. Phase 1: Skill Selection

1. Scan the skill listing of the session (and the skill folders of the workspace) and compare each `description` with the request. Do not rely on a remembered list; skills are added and removed.
2. For each subtask, apply every skill whose trigger matches, and prefer the best fit among overlapping skills. There is no cap on the number of skills.
3. Announce each as "Using [skill] to [purpose]".
4. A skill with no matching trait is not invoked.
5. Repeat the scan at each phase boundary, since a later phase can reveal a new trait.

The table shows common matches. It is a seed, not a closed list.

| Content trait | Skills |
| --- | --- |
| Authoring or restructuring documents | `technical-doc-author`, `doc-coauthoring`, `humanizer` |
| Diagrams | `mermaid-diagram-guide`, `plantuml-guide` |
| Java code or tests | `test-driven-development`, `karpathy-guidelines` |
| Review of a diff or architecture | `code-architect-auditor`, `requesting-code-review`, `receiving-code-review` |
| Missing grounding source | `grounding-source-finder`, `exa-search`, `gpt-researcher-guide` |
| Explanation or mock defense | `tech-mentor`, `grill-me`, `socratic-tutor` |
| Defect of unknown cause | `systematic-debugging` |
| Independent parallel work | `dispatching-parallel-agents` |
| Creating or changing a rule, skill or prompt | `skill-forge`, `writing-skills` |

## 3. Phase 2: Plan

1. List the units (one unit is 1 document subsection, 1 Java class, or 1 paired Class + Test), their order, the verification command of each unit, and the risk class.

| Risk class | Criteria | Reviewers per round |
| --- | --- | --- |
| High | Changes the meaning of a business rule, formula, identifier (`FR-*`, `UC-*`, `BR-*`) or architecture | At least 2 independent subagents |
| Low | Changes only formatting, table structure, cross-references or lint findings | At least 1 subagent |

2. When the class is unclear, treat it as High.
3. A plan is large when it adds, removes or renames an identifier, or has more than 3 units. A large plan is shown to the user and approved before Phase 3. A small plan starts immediately.
4. A new decision (rule, formula, changed meaning, changed scope, architecture choice) is asked one question at a time with four parts: the nature of the issue, a concrete example with dates and numbers, an options table, and a recommendation with its source. A recommendation is never recorded as a decision before the user answers. An approved decision is written directly into the target deliverable; no separate ledger exists.
5. Work that creates no decision (lint fixes, column counts, running verification) is done without asking. When unsure whether work creates a decision, treat it as one and ask.

## 4. Phase 3: Execution Loop

Repeat for each unit, one unit per turn (R1):

1. Build the scaffold: headings and labeled placeholders only (rule 05 step B).
2. Elaborate the unit to match the scaffold.
3. Run the verification command (test, linter, regex count, diff) and record the real output (R3). When the unit wrote or edited a Markdown file, by the agent or by a subagent, also run every check of rule 08 section 7 on that file (FILE is the path of the Markdown file, and STRIPPED is built with the first command of that section), under that section's own pass criteria, including its exceptions and the checks counted by reading. The `diff -w -B ORIG FILE` check applies only to a formatting-only fix with ORIG saved before the fix; for a new file or a content edit, record it as skipped. The unit is not finished while any other check fails.
4. Dispatch fresh reviewer subagents that do not inherit the writing context. Each reviewer receives only the product, the criteria and the sources. The number and function of reviewers is chosen to fit the task (for example one per quality dimension: correctness, traceability, style, tests), with no upper limit. The risk class sets the minimum, not the maximum.
5. Reviewers also check the reasoning of R6: every conclusion has all four links, each cited source supports the conclusion, and no conjecture is written as fact. A broken link is reported with the conclusion and the source passage quoted.
6. Every finding carries a location, a quoted text and a count. A finding without evidence is rejected.
7. Fix every accepted finding, then start a new round with new reviewers.
8. The loop has no round cap. It ends when every reviewer of the round reports 0 findings. When rounds stop converging (the same finding returns after a fix), stop and report the cause to the user instead of repeating.
9. A finding that needs a decision returns to Phase 2 item 4.

Subagents are used wherever they help: research, drafting independent units, review, sweeps. The agent chooses their number and role per task. Constraints:

1. Subagents take independent work only and share no mutable state.
2. A subagent never dispatches subagents of its own.
3. Parallel subagents never write the same file.
4. A reviewer is never the agent that wrote the unit.

## 5. Phase 4: Whole-Scope Sweep

1. After all units are done, search the whole active branch (every directory returned by listing it) for every identifier and term that changed, including old variants.
2. The count of unhandled matches must be 0. Keep the command and the count as evidence.
3. A match outside the declared scope is reported to the user and not fixed in this run.
4. Re-run every check of rule 08 section 7 on every Markdown file the run created or edited, because later edits can break an earlier pass. Fix each failing check in those files and re-run, then keep the per-file counts as evidence. The ORIG diff check follows the same skip rule as Phase 3 item 3. Rule 08 prevails over the smallest-region limit (section 7 item 2 of this rule) for formatting defects in a file the run edited, as ranked in row 4 of the Precedence table; an untouched file is not rewritten (rule 08 section 1.5).

## 6. Phase 5: Handover

1. Report per unit: result, verification command with output, open findings, and a suggested next step. For each Markdown file, also report the rule 08 results.
2. Never state that work is complete, aligned or error-free without the evidence of R3.

## 7. Safety Boundaries

1. Delete, overwrite, commit and push only when the user asks explicitly.
2. Read the target immediately before editing it, and change only the smallest region the unit needs.
3. Edit only the active branch. Read the workspace root README for the current write permissions of other branches and shared folders, and treat them as read-only unless the user names them as the target.
4. Do not rely on remembered paths. List the directory or search before reading or editing, because files and folders change continuously.
