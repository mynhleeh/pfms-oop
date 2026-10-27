---
trigger: always_on
description: "Source attribution discipline: separates chat-level source pointers and inference labels from detailed citations (APA) written into the relevant deliverable's own citation section."
---
# 07. Source Attribution & Citation Discipline

This rule defines where and how sources are reported. It complements R2 and R3 in `00-core-charter.rule.md` and the grounding hierarchy in `01-academic-grounding.rule.md`, and never relaxes them. When it conflicts with a rule that ranks higher in the Precedence table of `00-core-charter.rule.md`, the higher rule prevails and the conflict is reported to the user. Rules in the same row are ordered 07 before 06.

## 1. Three Separate Channels

Information about sources belongs to exactly one of three channels. Never mix their formats.

| Channel | Where it lives | Content | Format |
| --- | --- | --- | --- |
| Chat pointer | The chat reply only | Where a fact came from during the work: repo path, section, page, or web address | Short and informal: path or link, optionally with a section or page |
| Inference and notes | The chat reply only | Reasoning of the agent, assumptions, trade-offs, uncertainty, other side information | Explicitly labeled as inference or note |
| Detailed citation | The citation section of the relevant deliverable | Bibliographic record of every source the deliverable relies on | Formal citation style (APA by default) |

## 2. Chat Pointers and Inference Labels

1. Every factual or numeric claim in a chat reply names its source in the same reply, in the form `file path (section or page)` or a web address. A claim that has no source is not stated as fact; it is labeled as inference or the user is asked (R2).
2. A statement that the source contains is marked as source content. A statement the agent derived from the source is marked as inference. The two are never written in the same sentence without the label.
3. Pointers stay in chat. They are not copied into deliverables as paths or links.
4. Assumptions, trade-offs and extra information are written as separate short notes, not mixed into the pointer.

## 3. Detailed Citations in Deliverables

1. When a deliverable states content that rests on an external or internal source, the source is cited in that deliverable's own citation section. Cite in the document the content belongs to, not in a neighboring one. Example: a point about topic B drafted in a requirements document and found in file X is cited in the citation section of that requirements document.
2. Find the citation section by reading the document. Do not assume its number or title. When the document has none, ask the user where it belongs before writing the citation.
3. Use APA (current edition) unless the document already uses another style, in which case follow the document. Every entry carries the fields that the style requires: author or organization, year, title, publisher or venue, and a stable link or identifier when one exists.
4. Cite the original source. When only a secondary summary was read, say so in chat and cite the summary as read.
5. The body text of the deliverable refers to a citation by its in-text marker in the document's style. Repo paths, working notes and chat-style pointers do not appear in the body.
6. Add each source once. Before adding, read the citation section and reuse an existing entry instead of duplicating it.
7. Never invent bibliographic fields. A missing field (year, publisher, identifier) is reported to the user in chat, and the entry is completed after the user answers or the field is verified.

## 4. Verification

1. Reviewer subagents in rule 06 check that every in-text marker has an entry, every entry is used at least once, and each cited source actually contains the content attributed to it. A finding quotes the marker, the entry and the source passage.
2. The count of markers without entries and of entries without markers must be 0. Keep the command or search and its count as evidence (R3).
3. In the final chat reply, list the sources added to or changed in each deliverable, so the user can review them without opening the file.
