import re
import sys


def strip(raw):
    out = []
    fm = False
    infence = False
    opener = ''
    for i, line in enumerate(raw.split('\n')):
        if i == 0 and line == '---':
            fm = True
            continue
        if fm and line == '---':
            fm = False
            continue
        if fm:
            continue
        m = re.match(r'^ *(`{3,}|~{3,})', line)
        if m:
            token = m.group(0).replace(' ', '')
            if not infence:
                infence = True
                opener = token
                out.append('@@FENCE@@')
            elif token[0] == opener[0] and len(token) >= len(opener) and re.match(r'^ *[`~]+ *$', line):
                infence = False
            continue
        if infence:
            continue
        out.append(re.sub(r'`[^`]*`', 'X', line))
    if infence:
        out.append('@@UNCLOSED@@')
    return '\n'.join(out)


def count(rx, text):
    return len(re.findall(rx, text, re.M))


def main(path):
    raw = open(path, 'rb').read().decode('utf-8')
    st = strip(raw)
    bad = []

    multi = {'2.2', '2.3a', '2.3b', '2.3c', '4.4a', '4.4b', '4.4c', '5.2a', '5.2b', '5.3c', '5.3d', '5.6a', '5.6b'}

    def chk(name, rx, text=None):
        body = st if text is None else text
        if name in multi:
            n = count(rx, body)
        else:
            n = sum(count(rx, one) for one in body.splitlines())
        if n:
            bad.append((name, n))

    chk('2.1', r' +$|\t+$')
    chk('2.2', r'\n\n\n')
    chk('2.3a', r'\n\n\Z', raw)
    chk('2.3b', r'[^\n]\Z', raw)
    chk('2.3c', r'\A\s*\n', raw)
    chk('2.4', r'\t')
    chk('2.5', r'^[^|]*[^| ] {2,}[^ ]')
    chk('3.1', r'^\s*([-*_])(\s*\1){2,}\s*$')
    chk('3.3', r'^=+\s*$')
    chk('4.1a', r'[^| ] {2,}\|')
    chk('4.1b', r'\| {2,}[^| ]')
    chk('4.1c', r'(?<!\x5c)\|[^| ]|[^| \x5c]\|')
    chk('4.2a', r'\|\s*:?-{4,}:?\s*(\||$)')
    chk('4.2b', r'\|\s*:?-{1,2}:?\s*(\||$)')
    chk('4.2c', r'^\|[:-]')
    chk('4.3', r'\| {3,}\||\|\||\| \|')
    chk('4.4a', r'^[^|\n][^\n]*\n\|')
    chk('4.4b', r'^\|[^\n]*\n[^|\n]')
    chk('4.4c', r'^\|[^\n]*\n\n\|')
    chk('4.5', r'^\|.*[^|\s]\s*$')
    chk('5.1a', r'^#{1,6}[^# ]')
    chk('5.1b', r'^#{1,6} .*\s#+\s*$')
    chk('5.2a', r'[^\n]\n#{1,6} ')
    chk('5.2b', r'^#{1,6} [^\n]*\n[^\n]')
    chk('5.3a', r'^\s*[*+] ')
    chk('5.3b', r'^\s*(-|\d+\.)  +\S')
    chk('5.3c', r'^\s*- [^\n]*\n\n\s*- ')
    chk('5.3d', r'^\d+\. [^\n]*\n\n\d+\. ')
    chk('5.5', r'(^|\s)\*\*(\s|$)|\*\*\*\*')
    chk('5.6a', r'[^\n]\n@@FENCE@@')
    chk('5.6b', r'@@FENCE@@\n[^\n]')
    chk('5.6c', r'@@UNCLOSED@@')
    chk('6.1a', r'&(#\d+|#x[0-9a-fA-F]+|[A-Za-z]+);')
    chk('6.1b', '→|⇒')
    chk('6.2a', ' |​', raw)
    if raw.startswith('﻿'):
        bad.append(('6.2b', 1))
    crlf = len(re.findall(r'\r$', raw, re.M))
    lf = len([x for x in raw.split('\n') if x != '' and not x.endswith('\r')])
    if crlf and lf:
        bad.append(('2.6', crlf))
    heads = [len(m.group(1)) for m in re.finditer(r'^(#{1,6}) ', st, re.M)]
    jumps = sum(1 for a, b in zip(heads, heads[1:]) if b - a > 1)
    if jumps:
        bad.append(('5.2-jump', jumps))
    inside = False
    untagged = 0
    for x in raw.split('\n'):
        m = re.match(r'^ *(`{3,})(.*)$', x)
        if m:
            if not inside:
                inside = True
                untagged += m.group(2).strip() == ''
            else:
                inside = False
    if untagged:
        bad.append(('5.6-tag', untagged))
    name = path.replace(chr(92), '/').split('/')[-1]
    print(('PASS' if not bad else 'FAIL ' + str(bad)), name)
    return 1 if bad else 0


if __name__ == '__main__':
    sys.exit(main(sys.argv[1]))
