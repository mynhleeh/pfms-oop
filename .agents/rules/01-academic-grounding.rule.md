---
trigger: always_on
description: "Hierarchical knowledge grounding: 4-tier grounding hierarchy discovered by role (not by fixed path), multi-branch dynamic resolution cascade, and dynamic document registration."
---

# 01. Academic & Domain Knowledge Grounding

All analyses, documentation updates, and software engineering implementations must be rigorously grounded in verified repository sources. Unsubstantiated claims and speculative inventions are strictly forbidden.

## 1. The 4-Tier Grounding Hierarchy

The repository layout changes continuously. Tiers are defined by role. At the start of each task, inspect the workspace root, the active branch directory and their `docs/` subtrees, then map what actually exists to the tiers below. Never assume a fixed file name, folder name or section number. State the mapping you found (path per tier) before relying on it.

```
[Tier 1: Academic Authority]      assignment, syllabus, rubrics, grading criteria
               │
               ▼
[Tier 2: Requirements Authority]  the branch's requirements deliverable
               │
               ▼
[Tier 3: Technical Standards]     reference material: exemplary reports, patterns, standards
               │
               ▼
[Tier 4: Project Deliverables]    analysis, design, source code and tests of the branch
```

1. **Tier 1 - Academic Authority:**
   - Role: the assignment statement, grading rubrics and thesis criteria. Typically found under an `academic` folder of the branch or the workspace root.
   - Obligation: maximize alignment with rubric standards. Never omit a mandatory assignment deliverable.

2. **Tier 2 - Requirements Authority:**
   - Role: the branch's requirements deliverable (SRS and use cases). It is the highest authority for business rules, formulas, scope and terminology. Every downstream deliverable must trace back to it.
   - Decisions agreed with the user are written into this deliverable. No separate decision ledger or glossary store is maintained.
   - If the deliverable is ambiguous or silent on a point, STOP AND ASK per R2, then update the deliverable once the user answers.
   - A branch without a requirements deliverable may draft it from seed material found in its own docs (for example a domain workbook and narrative) together with Tier 3. Once the deliverable exists, the seed material is no longer consulted for business rules.

3. **Tier 3 - Technical Standards:**
   - Role: reference projects, industrial patterns, formatting templates. The workspace-level `reference` folder is the shared location.
   - Obligation: structural benchmark for typography, section breakdown and diagram clarity. Never authoritative over business-rule content.

4. **Tier 4 - Project Deliverables:**
   - Role: everything the branch produces below the requirements layer (analysis, design, source, tests).
   - Obligation: bidirectional traceability to Tier 1 and to the requirements deliverable. Every use case, domain entity and class maps to a business rule or academic objective.

## 2. Multi-Branch Dynamic Resolution Protocol

The workspace houses several branch directories. Discover them by listing the workspace root and reading the root README; do not rely on a fixed list.

### 2.1. Active Branch Auto-Detection (`ACTIVE_BRANCH`)

- Infer the active branch from the path of the file being edited or discussed.
- When the request names no path, list the candidate branches and ask the user once.

### 2.2. Local-Over-Global Resolution Cascade

1. **Level 1 (Branch-Local, highest priority):** search the active branch's `docs/` subtree.
   - A document that exists only in one branch is the authoritative local benchmark for that branch.
2. **Level 2 (Workspace Shared):** search the shared reference folder at the workspace root, then the other branches' `docs/` as a last resort.

### 2.3. Dynamic Document Discovery & Drift Warning

- Inspect the directory tree (listing and search) at session start and again whenever a path fails to resolve. A path remembered from an earlier session is a hint, never a fact.
- When citing a branch-local file, state its scope: `Based on document X (local to branch B)...`.
