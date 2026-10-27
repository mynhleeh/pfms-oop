---
name: commit-slicer
description: Analyzes the current uncommitted changes (or a given diff/branch) in a project, groups the changed files/folders into cohesive commit-sized units, and drafts a conventional commit message (type/scope/description) for each group. Trigger when the user asks to split changes into commits, propose commit messages, organize a messy working tree before committing, or review "what changed and how should I commit it" for a branch/folder. Do NOT trigger for writing a single commit message for an already-obvious single-purpose change, or for actually running `git commit` (that stays a manual, confirmed step).
version: 1.0.0
tags: [git, commit, conventional-commits, diff-analysis, code-review, workflow]
---

# Commit Slicer

## When to Activate

Activate when the user asks things like:
- "Phân tích thay đổi trong branch/folder X và gợi ý nhóm commit"
- "Split my changes into logical commits"
- "What commit messages should I use for these files?"
- "Review thay đổi hiện tại và đề xuất commit message theo type/scope"

Do **not** activate for:
- A single, already-obvious one-purpose diff (just write one commit message directly).
- Actually executing `git add` / `git commit` — this skill only proposes; committing is a separate, user-confirmed action per the workspace's git-safety rules.

## Overview

Turns a working tree (or a named branch/folder/diff range) into a small number of independently reviewable, logically coherent commits. Every grouping decision must be traceable to evidence from `git status`/`git diff`, never guessed from file names alone.

## Instructions

### Step 1 — Scope the target
Determine what "changes" means from the user's phrasing:
- No target named → default to the current working tree (`git status` + `git diff` against `HEAD`).
- A branch/folder named → `cd` into it (or use `-C <path>`) and run the same commands scoped there.
- A commit range or PR given → use `git diff <base>...<head>` instead.

Run, in parallel:
```
git status --porcelain=v1
git diff --stat
git diff -M --stat        # detect renames explicitly; a rename is never a plain add+delete
```
If the repo has no commits yet or the target path is not a git repo, say so and stop (do not fabricate history).

### Step 2 — Classify every changed path
For each path in the status output, tag it with:
1. **Change kind**: added / modified / deleted / renamed (renamed only if `-M` confirms similarity — never assume a delete+add pair is a rename without checking).
2. **Layer/domain**: infer from directory structure and project rules (e.g., docs vs src vs config vs generated/reference material), not from filename guesses alone.
3. **Coupling**: does this file's change only make sense together with another changed file (e.g., a class and its test, a schema and its migration, a moved file's old and new location)?

If a project has explicit layering rules (CLAUDE.md, `.claude/rules/*`, README conventions), those rules are the authority for how to split "requirements" vs "design" vs "domain data" — read them before grouping if present.

### Step 3 — Form groups (the Anti-Mixing Rule)
Merge paths into the **fewest groups such that no group mixes unrelated concerns**. Concretely:
- One group per distinct concern: a structural move, a content rewrite, a dependency/config bump, a data/reference addition are always separate groups even if they touch overlapping folders.
- Never split a single coupled change (e.g., class + its test, or an old-path delete + new-path add that are the same content) across two groups.
- A directory whose files are explicitly "not yet finalized" (user says so, e.g., "still OCR-ing", "draft", "WIP") becomes its own **"hold — do not commit"** group, called out separately, never silently included in another group.
- Cap at 3-5 groups for a normal change set; if more than 5 emerge, say so and ask whether to proceed with that many or scope down.

### Step 4 — Draft one commit message per group
Use Conventional Commits shape: `type(scope): short imperative summary`, blank line, 1-3 sentence body explaining *why*, not a restatement of the diff stat.
- `type` ∈ `feat|fix|refactor|docs|chore|test|style|build|ci|perf` — pick the one matching the dominant intent of the group, never default to `chore` for content rewrites.
- `scope` = the shortest path segment that uniquely identifies the affected area (e.g., `domain`, `analysis`, `req-uc`), not the full file path.
- Body states the reason for the change (what problem it solves / what decision it reflects), and flags explicitly if the change is a non-rename replacement (content differs from any prior file at a similar path) so the reviewer doesn't assume `git mv` semantics.
- If the group's actual diff content contradicts what a plain file listing would suggest (e.g., "renamed" file has substantially different content), state that discrepancy in the message body.

### Step 5 — Order and caveats
Propose a commit order: lowest-risk / most-independent groups first, most content-heavy or hardest-to-review group last. State any group that should be reviewed by the user before committing (e.g., large rewrites, ambiguous renames). Never invoke `git add`/`git commit` yourself unless the user explicitly asks you to execute it — proposing is the default contract.

## Output Contract

Emit exactly one Markdown block per invocation with these sections, in the user's language:

```
## Nhóm N — <one-line label>
- File: <path>, <path>, ...
\`\`\`
type(scope): summary

Body (why, 1-3 sentences).
\`\`\`

## Giữ nguyên / không commit (nếu có)
- <path> — <reason, quoting the user's own stated reason if given>

## Thứ tự đề xuất
1. Nhóm ...
```

Do not add a "next steps" essay beyond stating the proposed order and any explicit confirmation needed before committing.

## Anti-Examples

| ❌ Avoid | ✅ Prefer | Why |
|---|---|---|
| Treating a deleted file + a new file at a different path as an automatic rename | Run `git diff -M --stat` and only call it a rename if similarity is reported | A same-named refactor can still be a full rewrite, not a move |
| One giant group "update everything" | Separate groups per distinct concern (structure move vs content rewrite vs data cleanup) | Reviewable, revertable commits |
| `chore: update docs` for a substantive rewrite | `docs(req-uc): rewrite use-case requirements for X` with a why-body | `type` must reflect actual intent, not be a lazy default |
| Silently committing a folder the user said is still in progress (e.g., mid-OCR) | List it under "Giữ nguyên / không commit" with the user's own stated reason | Respects explicit scope limits (Principle 4: do exactly what's asked) |

## Common Issues

- **Repo not detected / not a git repo at the target path** — say so, ask whether to `git init` (do not do it silently).
- **More than ~5 natural groups** — flag it and ask whether to proceed with that many or narrow scope, rather than silently merging unrelated concerns to hit a smaller number.
- **Binary/reference/generated files mixed with source** — keep them in their own group; call out clearly if the user has indicated that folder is still being produced (e.g., OCR output) and should be excluded from commit proposals entirely.

## References

- `references/grouping-heuristics.md` — worked heuristics for coupling detection (rename vs rewrite, coupled pairs, generated-content exclusion) — read when a diff has ambiguous file relationships.
