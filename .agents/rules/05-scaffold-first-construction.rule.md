---
description: "Scaffold-first construction protocol for authoring new documents or code from zero: mandatory outline-then-elaborate loop, queue/stack build ordering, post-build verification loop."
globs: ["**/*.md", "**/*.java", "**/*.qmd"]
---

# 05. Scaffold-First Construction Protocol

Applies when creating a NEW document, a major section (H2/H3 or higher), or a source code module FROM ZERO (no prior draft exists). This does not replace R1 (00-core-charter.rule.md) when editing existing content; the two rules are complementary. R1 applies to every editing turn; this rule applies additionally when initiating new content.

## 1. No Monolithic Generation

- Generating complete, end-to-end content for any document or module beyond 1 coherent unit (per R1's definition: 1 document subsection, 1 class, or 1 paired Class+Test) in a single response turn is strictly forbidden.
- The scaffold produced in Step B of the loop in Section 2 does not count as a violation of this rule, since it contains only placeholders and headings, not fully elaborated business content.

## 2. Mandatory 3-Step Loop

Every turn that builds new content must pass through all 3 steps in order; no step may be merged or skipped.

| Step | Name | Mandatory output | Countable completion criterion |
| --- | --- | --- | --- |
| A | Knowledge & information identification | A list of knowledge sources used for the content unit, with an assessment of sufficiency (sufficient / insufficient, and what is missing) | Every content unit names at least one explicit knowledge source; "will infer as needed" is not acceptable |
| B | Scaffold construction | A skeletal structure: headings and a placeholder for each part, with no fully written sentences | Every placeholder carries a label describing the content it will hold |
| C | Progressive elaboration | Fully elaborated content filling the scaffold, for exactly 1 coherent unit | The elaborated content matches the placeholders declared in Step B; no section is added beyond the scaffold without first returning to Step B |

Step C must never be performed for a content unit that has not yet completed Step B.

## 3. Queue & Stack Build Ordering

1. **Queue (FIFO)** applies to peer content units with no dependency on one another. Example: system X consists of units A, B, C with no coupling; build them in the order declared in Step A, following A then B then C, and never reorder them arbitrarily between turns.
2. **Stack (LIFO)** applies when a mandatory dependency is discovered. If building unit A reveals that A requires unit B as a foundation and B is not yet DONE: push A into a BLOCKED state, switch to building B first through the full 3-step loop in Section 2, and only pop A back for continuation once B reaches DONE. The stack may nest across multiple levels (if B itself needs C, push B, build C first, then unwind C then B then A).
3. **Never mix the two models without declaring which one applies:** at every point where the next unit is chosen, state explicitly whether Queue or Stack is in effect and why. Dependency must never be invoked as a pretext to reorder the queue when no real dependency exists.

## 4. Explicit Scaffold State Tracking

- The scaffold for the whole document or module must be kept in an inspectable form: every unit carries a state of PENDING / SCAFFOLDED / DRAFTING / BLOCKED / DONE, stated in chat at each turn.

## 5. Post-Build Verification and Continuous Improvement

After a unit reaches a provisional DONE (Step C complete), the following 4 steps are mandatory before the DONE state is locked and the next unit is started:

1. **Verify:** run the verification tool appropriate to the content type (linter, test suite, regex count, diff assertion, or a cross-check against the cited grounding sources) and record concrete evidence (figures, logs, PASS/FAIL). Statements such as "should be correct", "probably fine", or "looks like it passes" are forbidden in place of actual evidence.
2. **Quality assurance:** cross-check the elaborated unit against the scaffold from Step B and the knowledge sources from Step A, confirming no scaffold item was left unfilled.
3. **Assess:** compare the unit against the other rules active in the workspace (00-04, and this rule itself), confirming no violation.
4. **Optimize:** if verification or assessment surfaces an opportunity to tighten or improve the unit without losing any business content, return to Step C to revise it, then re-run verification. If nothing is found, record "0 findings" together with evidence that the check was actually performed, not skipped.

A unit's DONE state may only be locked once all 4 steps above carry evidence, not immediately after Step C produces content.
