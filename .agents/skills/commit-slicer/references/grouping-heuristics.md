# Grouping Heuristics

Detailed decision rules for Step 2-3 of `commit-slicer`, used when file relationships are ambiguous.

## Rename vs. rewrite

`git status` reports a deleted path and an added path as two separate lines; it never infers a rename on its own. To tell them apart:

```
git diff -M20% --stat -- <old-path> <new-path>
```

- If git prints `rename <old> => <new>` (or a `similarity index` above the threshold), treat it as a **move**: one group, commit type `refactor` or `chore`, scope names the moved area, body says "move X to Y, no content change" (verify with `git diff --stat` showing near-zero insertions/deletions).
- If git still prints separate `delete`/`create` entries even at a low similarity threshold (e.g., 20%), the files are **unrelated in content** despite similar names/paths. Treat as two independent changes: a deletion (with its own reason) and a new addition (with its own reason) — or, if they serve the same conceptual role but were rewritten, a single group with a body that explicitly says "content rewritten, not a rename."

## Coupled pairs (never split across groups)

- A source file and its matching test file changed together.
- A schema/interface change and every call site it forces to change.
- An old-location delete and a new-location add that are confirmed the same content (true rename, see above).
- A moved reference/config file and the code that points to its new path.

## Generated / reference / hold-out content

Directories that are explicitly transient or externally sourced (OCR output, vendored docs, generated build artifacts, scratch/draft folders) get their own group labeled "hold — do not commit" whenever the user has said the content isn't finalized. Never fold such a directory into a content or structure group even if it was technically touched in the same working-tree snapshot.

## Picking `type`

| Signal in the diff | type |
|---|---|
| New capability/file serving a new requirement | `feat` |
| Behavior-preserving restructuring (move, rename, extract) | `refactor` |
| Correcting an existing wrong behavior | `fix` |
| Docs/spec/requirements content only | `docs` |
| Dependency, config, tooling, non-behavioral housekeeping | `chore` |
| Test-only changes | `test` |

Never default to `chore` just because a change is hard to classify — re-read the diff body first; `chore` is for genuinely non-functional housekeeping only.
