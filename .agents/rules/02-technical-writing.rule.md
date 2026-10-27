---
description: "Universal technical writing and Markdown documentation standards: table integrity, anti-AI tells, and abstraction purity."
globs: ["**/*.md", "**/*.qmd", "**/*.markdown"]
---

# 02. Technical Writing & Markdown Discipline

This rule enforces professional technical writing standards, pristine table formatting, and the elimination of machine-generated stylistic artifacts across all Markdown deliverables.

## 1. Abstraction Layer Discipline

- **Requirements Layer (SRS/PRD, the branch's requirements deliverable):** Focus exclusively on user and business needs (**WHAT**). Strictly forbid leakage of internal software design (class names, GoF pattern tags, physical database schemas) or implementation mechanics (Java method names, internal exception classes). Express edge cases and errors in clear, technology-agnostic domain terminology.
- **Analysis & Architecture Layer (OOA/OOD deliverables of the branch):** Focus on domain structure and software realization (**HOW**). Explicitly link components to functional requirements via tracking IDs (`UC-*`, `FR-*`, `BR-*`).

## 2. Anti-AI Tells & Stylistic Rules

1. **Linguistic Purity (Monolingual Body Text):**
   - In Vietnamese text, do NOT append parenthesized English terms immediately following translated terms (e.g., forbid `Tổng quan (Overview)`, `Yêu cầu (Requirement)`).
   - Write natural Vietnamese: `Tổng quan`, `Yêu cầu`. Preserve English for proper nouns and standard technology terms: `Kafka`, `Docker`, `Java`, `PostgreSQL`, `gRPC`.
2. **Zero Em-Dash as Sentence Punctuation:**
   - The count of em-dash `—` or double hyphen `--` acting as sentence breaks outside code blocks must equal **0**. Use periods to create concise sentences, or standard commas/colons.
3. **No Mechanical Bolded List Headers:**
   - Forbid robotic bold prefixes on bullet items: `- **Header:** Explanation text`. Use standard descriptive sentences, numbered hierarchy, or structured comparison tables.
4. **No Fake Contrasts & Parallel Triplets:**
   - Forbid rhetorical formulas such as "Not only X, but also Y" and forced rhythmic triplets ("fast, robust, and scalable"). State concrete metrics and capabilities directly.
5. **Forbidden AI Vocabulary (Zero Tolerance):**
   - Forbid: `delve`, `tapestry`, `landscape` (metaphorical), `pivotal`, `seamless`, `leverage`, `game-changer`, `đòn bẩy`, `bức tranh tổng thể`, `toàn diện và sâu sắc`, `trực quan sinh động`, `chìa khóa vàng`.

## 3. Markdown Table Integrity

1. **Strict Column Count Parity:**
   - Number of header columns = Number of delimiter columns (`|---|`) = Number of data row columns across every single row.
   - Tables with missing or extra cells are rejected immediately.
2. **Pipe Character Escaping:**
   - Any pipe character `|` inside mathematical formulas (`$A \| B$`) or code snippets within table cells must be escaped as `\|`.
3. **Empty Cells:**
   - Represent empty cells with a single whitespace ` |  | `; never insert em-dashes `—`.

## 4. Content Density & Presentation Form Discipline

1. **Select the presentation form based on content shape; never default to a single form:**

   | Content shape | Appropriate form |
   | --- | --- |
   | Multiple items sharing 2 or more repeating attributes in the same structure (e.g., name + type + constraint) | Table |
   | A sequence of steps with a mandatory execution order (process, handling scenario) | Numbered list |
   | A set of peer items with no mandatory order, each under one line | Bullet list |
   | An independent idea that does not repeat the structure of surrounding content | Short paragraph, on its own line |
   | A dataset with uniform attributes plus one long explanatory sentence per item | Table for the fixed attributes, combined with a short paragraph right below it for the extended explanation |

   Do not use a table for narrative or chronologically sequential content, or when the table would effectively collapse to a single data column (a sign the table is substituting for a list).

2. **Limit table density within a single major section (H2/H3):** Within one H2/H3 section, do not place more than 2 consecutive tables without a paragraph, list, or sub-heading between them. If a section has 3 or more table-shaped datasets, interleave a sub-heading or a one-sentence lead-in between the tables.

   This does not apply when each table already belongs to a distinct content unit with its own sub-heading (for example, a specification repeated under a fixed template for each requirement item). In that case, each unit's own sub-heading already satisfies the "interleaving" condition.

3. **Do not restate in prose what an adjacent table or diagram already states in full:** When a table or diagram annotation has already listed a set of information completely, the paragraph immediately before or after it may only add new information or give a brief cross-reference, never repeat the same content as prose.

   Mandatory exception: purposeful repeating structure where a document uses a fixed template to describe multiple content units of the same kind, to support independent lookup of each unit. In that case, every unit repeating the same set of subsections is a structural requirement, not stylistic redundancy.

4. **A prose paragraph must not exceed 6-8 consecutive lines** without being split, converted to a table, or converted to a structured list per the criteria in item 1.
5. **Never cut or paraphrase strings that carry traceability codes** (e.g., requirement IDs, use-case IDs, business-rule IDs, error codes), even when they look repetitive. This is a mandatory requirements-traceability mechanism, not stylistic redundancy.
