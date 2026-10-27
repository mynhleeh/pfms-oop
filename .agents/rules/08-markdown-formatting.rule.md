# 08. Markdown Physical Formatting

This rule governs the physical form of every Markdown file an agent creates or edits: whitespace, blank lines, dashes, entities and table spacing. It never changes the meaning of the content. Wording, tone and table design remain under rule 02.

## 1. Scope and Precedence

1. The rule applies to every `.md` file written or edited in this workspace, including rule files and skill files.
2. A formatting-only fix must not alter text. After the fix, the text with all whitespace removed is identical to the original, except for deleted thematic-break lines, converted entities, normalized table delimiter rows and setext headings converted to `#`. The last command of section 7 checks this.
3. Every check in section 7 runs before a Markdown file is reported as finished. A count above 0 means the file is not finished.
4. Where rule 02 and this rule disagree about the physical form of a file, this rule applies and the conflict is reported to the user. The ranking is in the Precedence table of `00-core-charter.rule.md`.
5. Files that existed before this rule (for example the `---` separator lines in rules 00 to 07) are brought into line when the unit that edits them is processed. The check runs on the edited file only, and an untouched file is not rewritten for formatting alone.

## 2. Whitespace

1. No line ends with a space or tab.
2. Never two or more consecutive blank lines. Exactly one blank line separates two blocks (heading, paragraph, list, table, code fence).
3. The file starts with content on line 1 and ends with exactly one newline character, with no blank line after the last content line.
4. No tab characters outside code fences. A nested list item indents to the text column of its parent marker: 2 spaces under `-`, 3 spaces under `1.`. This item is checked by reading.
5. One space between words and after punctuation. Two or more consecutive spaces between non-space characters are forbidden outside code fences. Table cells follow section 4.
6. Line endings are consistent within a file. A file with LF endings never receives CRLF lines.

## 3. Dashes and Thematic Breaks

1. No thematic break (`---`, `***`, `___`, or spaced variants) appears in the body of a document. Headings alone separate sections.
2. The only permitted `---` lines are the two YAML frontmatter delimiters and the delimiter row of a table.
3. No decorative runs of `=`, `-`, `*` or `_` on their own line. A line of `=` under text is a setext heading and is forbidden; write headings with `#`.
4. A document never ends with a dash line.

## 4. Tables

1. Compact form: one space between each pipe and the cell text, as in `| value | value |`. Cells are never padded with extra spaces to align columns.
2. The delimiter row uses exactly three hyphens per column, with a space after each pipe: `| --- | --- |`. Alignment colons are added only when alignment is required: `| :--- | ---: |`.
3. An empty cell holds one space of content, which gives two spaces between the pipes (`|  |`), as rule 02 section 3 requires.
4. A table has one blank line before and one after, and no blank line inside.
5. Every row starts and ends with a pipe.

## 5. Headings, Lists and Emphasis

1. A heading is `#` characters, one space, then text, with no closing `#` characters.
2. A heading has one blank line before and after it. Heading levels descend by one step at a time (H2 is followed by H3, never H4).
3. Unordered lists use `-` as the only marker. Ordered lists use `1.`, `2.`, `3.` in sequence. One space follows the marker.
4. Items of one list have no blank line between them.
5. Bold is `**text**` and italic is `*text*`, with no space inside the markers. Empty markers (`****`) are forbidden.
6. A fenced code block uses three backticks, carries a language tag (`text` when none applies), and has one blank line before and after. Tilde fences are not used.

## 6. Characters

1. No HTML entity in prose or tables. Write `>`, `<` and `&` directly, and write an arrow as `->`. Inside a code span or fence, text is kept literally.
2. No non-breaking space (U+00A0), zero-width space (U+200B) or byte-order mark inside a file.

## 7. Mandatory Verification

Run the commands with `rg` (ripgrep) and keep the counts as evidence (R3 in `00-core-charter.rule.md`). Any output from a command that has no `-c` flag is a failure, and the number of output lines is only a lower bound of the number of defects. A command with `-c` prints the number of matching lines, and a `-c` command that prints nothing counts as 0. Every expected count is 0, except the two exceptions named in the commands (2.6 and 1.2). The commands use a bare `|`, so copy them as written.

The first command creates STRIPPED, a copy without frontmatter, with each code fence (backtick or tilde, indented or not) replaced by the line `@@FENCE@@`, an unclosed fence reported as `@@UNCLOSED@@`, and each code span replaced by `X`. ORIG is the saved copy of the file before a formatting-only fix. Line numbers in STRIPPED differ from the original. Checks marked ORIGINAL run on FILE itself.

```text
awk 'NR==1&&/^---$/{fm=1;next} fm&&/^---$/{fm=0;next} fm{next} match($0,/^ *(\x60\x60\x60+|~~~+)/){m=substr($0,RSTART,RLENGTH);gsub(/ /,"",m);if(!f){f=1;o=m;print "@@FENCE@@"}else if(substr(m,1,1)==substr(o,1,1)&&length(m)>=length(o)&&$0~/^ *[\x60~]+ *$/){f=0};next} f{next} {gsub(/`[^`]*`/,"X");print} END{if(f)print "@@UNCLOSED@@"}' FILE > STRIPPED

# 2.1 trailing whitespace
rg -n ' +$|\t+$' STRIPPED
# 2.2 two or more blank lines in a row
rg -U -n '\n\n\n' STRIPPED
# 2.3 blank line at end of file, missing final newline, blank line on line 1 (ORIGINAL)
rg -U -n '\n\n\z' FILE
rg -U -c '[^\n]\z' FILE
rg -U -c '\A\s*\n' FILE
# 2.4 tab characters
rg -n '\t' STRIPPED
# 2.5 repeated spaces between non-space characters, outside table lines
rg -n '^[^|]*[^| ] {2,}[^ ]' STRIPPED
# 2.6 mixed line endings (ORIGINAL): a violation only when both counts are above 0
rg -c '\r$' FILE
rg -c -v '\r$' FILE
# 3.1, 3.3, 3.4 thematic break or decorative run, setext underline
rg --pcre2 -n '^\s*([-*_])(\s*\1){2,}\s*$' STRIPPED
rg -n '^=+\s*$' STRIPPED
# 4.1 padded table cell
rg -n '[^| ] {2,}\|' STRIPPED
rg -n '\| {2,}[^| ]' STRIPPED
rg --pcre2 -n '(?<!\x5c)\|[^| ]|[^| \x5c]\|' STRIPPED
# 4.2 delimiter cell with 4 or more hyphens or with fewer than 3, or without a space after the pipe
rg -n '\|\s*:?-{4,}:?\s*(\||$)' STRIPPED
rg -n '\|\s*:?-{1,2}:?\s*(\||$)' STRIPPED
rg -n '^\|[:-]' STRIPPED
# 4.3 empty cell with three or more spaces, with no space, or with one space
rg -n '\| {3,}\||\|\||\| \|' STRIPPED
# 4.4 table without a blank line before or after, or with a blank line inside
rg --pcre2 -U -n '^[^|\n][^\n]*\n\|' STRIPPED
rg --pcre2 -U -n '^\|[^\n]*\n[^|\n]' STRIPPED
rg --pcre2 -U -n '^\|[^\n]*\n\n\|' STRIPPED
# 4.5 table row that does not end with a pipe
rg -n '^\|.*[^|\s]\s*$' STRIPPED
# 5.1 heading without a space after the hash, or with closing hashes
rg -n '^#{1,6}[^# ]' STRIPPED
rg -n '^#{1,6} .*\s#+\s*$' STRIPPED
# 5.2 heading without a blank line before or after
rg --pcre2 -U -n '[^\n]\n#{1,6} ' STRIPPED
rg --pcre2 -U -n '^#{1,6} [^\n]*\n[^\n]' STRIPPED
# 5.3 non-dash bullet marker, more than one space after a marker, blank line between list items
rg -n '^\s*[*+] ' STRIPPED
rg -n '^\s*(-|\d+\.)  +\S' STRIPPED
rg --pcre2 -U -c '^\s*- [^\n]*\n\n\s*- ' STRIPPED
rg --pcre2 -U -c '^\d+\. [^\n]*\n\n\d+\. ' STRIPPED
# 5.5 bold with an inner space, or empty bold
rg -n '(^|\s)\*\*(\s|$)|\*\*\*\*' STRIPPED
# 5.6 fence without a blank line before or after, unclosed fence
rg --pcre2 -U -n '[^\n]\n@@FENCE@@' STRIPPED
rg --pcre2 -U -n '@@FENCE@@\n[^\n]' STRIPPED
rg -n '@@UNCLOSED@@' STRIPPED
# 6.1 HTML entity, named or numeric
rg -n '&(#\d+|#x[0-9a-fA-F]+|[A-Za-z]+);' STRIPPED
rg -n '→|⇒' STRIPPED
# 6.2 invisible characters (ORIGINAL); ripgrep strips a BOM, so the BOM uses grep
rg -n '\x{00A0}|\x{200B}' FILE
grep -c $'\xef\xbb\xbf' FILE
# 1.2 exception, formatting-only fix: every printed line must belong to one of four kinds: deleted thematic-break line, converted entity, normalized table delimiter row, setext heading converted to #
diff -w -B ORIG FILE
```

Three checks have no regex and are counted by reading.

| Check | Violation count formula |
| --- | --- |
| 5.2 heading level jump | Number of consecutive heading pairs where the next level minus the previous level is greater than 1 |
| 5.3 ordered list numbering | Number of ordered lists whose numbers are not 1, 2, 3 in sequence |
| 5.6 fence language tag | Number of opening fences with no text after the three backticks |

## 8. Common Generation Defects

| Defect | Typical cause | Required fix |
| --- | --- | --- |
| Blank line doubled between sections | Concatenating generated blocks that each end with a blank line | Join the blocks, then collapse to one blank line |
| `---` at the end of a file or between sections | Habit of closing a section with a divider | Delete the line |
| Table cells padded to equal width | Copying output of a table formatter | Rewrite each row in compact form |
| `-&gt;` or `&amp;` in text | HTML-escaped source pasted into Markdown | Replace with `->` and `&` |
| Trailing spaces after a sentence | Soft line breaks copied from a chat reply | Strip them |
