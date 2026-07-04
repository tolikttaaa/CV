#!/usr/bin/env python3
"""Parse LaTeX CV sources → web/cv-data.json"""

import json, os, re

REPO  = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LATEX = os.path.join(REPO, 'latex')
OUT   = os.path.join(REPO, 'web', 'cv-data.json')


def read(name):
    with open(os.path.join(LATEX, name), encoding='utf-8') as f:
        return f.read()


def brace_arg(text, pos):
    """Return (content, new_pos) for the next {}-arg starting at/after pos."""
    while pos < len(text) and text[pos] in ' \t\n':
        pos += 1
    if pos >= len(text) or text[pos] != '{':
        return '', pos
    depth, start, i = 0, pos, pos
    while i < len(text):
        c = text[i]
        if c == '\\':
            i += 2
            continue
        if c == '{':
            depth += 1
        elif c == '}':
            depth -= 1
            if depth == 0:
                return text[start + 1:i], i + 1
        i += 1
    return text[start + 1:], len(text)


def all_commands(text, cmd, nargs):
    """Yield nargs-tuples for every \\cmd occurrence (after stripping comments)."""
    text = _strip_comments(text)
    pat = re.compile(r'\\' + re.escape(cmd) + r'(?![a-zA-Z])')
    for m in pat.finditer(text):
        pos = m.end()
        args = []
        for _ in range(nargs):
            arg, pos = brace_arg(text, pos)
            args.append(arg)
        yield tuple(args)


def get_arg(text, cmd):
    m = re.search(r'\\' + re.escape(cmd) + r'(?![a-zA-Z])', text)
    if not m:
        return ''
    val, _ = brace_arg(text, m.end())
    return val


def get_2args(text, cmd):
    m = re.search(r'\\' + re.escape(cmd) + r'(?![a-zA-Z])', text)
    if not m:
        return '', ''
    a, pos = brace_arg(text, m.end())
    b, _   = brace_arg(text, pos)
    return a, b


def _strip_comments(text):
    return re.sub(r'(?m)(?<!\\)%.*$', '', text)


def to_html(text):
    text = _strip_comments(text)
    html = _conv(text)
    html = re.sub(r'[ \t\n]+', ' ', html).strip()
    html = html.replace(' -- ', ' – ').replace('--', '–')
    return html


def to_text(text):
    html = to_html(text)
    plain = re.sub(r'<[^>]+>', '', html)
    plain = (plain.replace('&amp;', '&').replace('&lt;', '<')
                  .replace('&gt;', '>').replace('&quot;', '"').replace('&#39;', "'"))
    return re.sub(r'\s+', ' ', plain).strip()


def _conv(text):
    out = []
    i, n = 0, len(text)
    while i < n:
        c = text[i]

        if c == '~':
            out.append(' '); i += 1; continue

        if c == '{':
            inner, new_i = brace_arg(text, i)
            out.append(_conv(inner)); i = new_i; continue

        if c != '\\':
            out.append(c); i += 1; continue

        # — backslash escape or command —
        if i + 1 >= n:
            i += 1; continue
        nc = text[i + 1]

        if nc == '\\':                          # line-break \\
            out.append(' '); i += 2
            while i < n and text[i] == '[':     # skip \\[skip]
                while i < n and text[i] != ']': i += 1
                if i < n: i += 1
            continue

        if not nc.isalpha():                    # \{ \} \& \% etc.
            if   nc == '{': out.append('{')
            elif nc == '}': out.append('}')
            elif nc == '&': out.append('&amp;')
            elif nc == '%': out.append('%')
            elif nc == '_': out.append('_')
            elif nc == '#': out.append('#')
            elif nc == ' ': out.append(' ')
            i += 2; continue

        j = i + 1
        while j < n and text[j].isalpha(): j += 1
        cmd = text[i + 1:j]; i = j
        while i < n and text[i] == ' ': i += 1  # eat trailing spaces

        if cmd == 'textbf':
            a, i = brace_arg(text, i)
            out.append(f'<strong>{_conv(a)}</strong>')
        elif cmd in ('emph', 'textit'):
            a, i = brace_arg(text, i)
            out.append(f'<em>{_conv(a)}</em>')
        elif cmd == 'link':
            url, i = brace_arg(text, i)
            lbl, i = brace_arg(text, i)
            out.append(f'<a href="{url}" target="_blank" rel="noopener">{_conv(lbl)}</a>')
        elif cmd == 'href':
            url, i = brace_arg(text, i)
            lbl, i = brace_arg(text, i)
            out.append(f'<a href="{url}" target="_blank" rel="noopener">{_conv(lbl)}</a>')
        elif cmd in ('textsc', 'mbox', 'textnormal', 'text',
                     'small', 'footnotesize', 'large', 'Large', 'LARGE'):
            a, i = brace_arg(text, i)
            out.append(_conv(a))
        elif cmd == 'begin':
            env, i = brace_arg(text, i)
            if env == 'itemize':   out.append('<ul>')
            elif env == 'enumerate': out.append('<ol>')
        elif cmd == 'end':
            env, i = brace_arg(text, i)
            if env == 'itemize':   out.append('</ul>')
            elif env == 'enumerate': out.append('</ol>')
        elif cmd == 'item':
            if i < n and text[i] == '[':
                while i < n and text[i] != ']': i += 1
                if i < n: i += 1
            out.append('<li>')
        elif cmd in ('hfill', 'enspace', 'quad', 'qquad', 'thinspace'):
            out.append(' ')
        elif cmd in ('hspace', 'vspace'):
            _, i = brace_arg(text, i)
        elif cmd in ('smallskip', 'medskip', 'bigskip',
                     'par', 'newline', 'linebreak',
                     'noindent', 'raggedright', 'centering'):
            pass
        elif cmd in ('color', 'textcolor'):
            _, i = brace_arg(text, i)          # color arg
            if cmd == 'textcolor':
                a, i = brace_arg(text, i)
                out.append(_conv(a))
        # unknown commands → silently skip without consuming args

    return ''.join(out)


# ── Social info ───────────────────────────────────────────────────────────────

def parse_social(text):
    handlers = {
        'smartphone': lambda v: {'type': 'phone',    'icon': 'phone',        'text': v},
        'telegram':   lambda v: {'type': 'telegram', 'icon': 'paper-plane',  'url': f'https://t.me/{v}',                        'text': f't.me/{v}'},
        'email':      lambda v: {'type': 'email',    'icon': 'envelope',     'url': f'mailto:{v}',                               'text': v},
        'linkedin':   lambda v: {'type': 'linkedin', 'icon': 'linkedin',     'url': f'https://www.linkedin.com/in/{v}',          'text': f'linkedin.com/in/{v}'},
        'leetcode':   lambda v: {'type': 'leetcode', 'icon': 'code',         'url': f'https://leetcode.com/u/{v}',               'text': f'leetcode.com/u/{v}'},
        'github':     lambda v: {'type': 'github',   'icon': 'github',       'url': f'https://github.com/{v}',                   'text': f'github.com/{v}'},
        'address':    lambda v: {'type': 'address',  'icon': 'location-dot', 'text': v},
    }
    pat = re.compile(r'\\(' + '|'.join(handlers) + r')(?![a-zA-Z])')
    result = []
    for m in pat.finditer(text):
        raw, _ = brace_arg(text, m.end())
        v = to_text(raw)                       # strip LaTeX escapes (e.g. \_)
        result.append(handlers[m.group(1)](v))
    return result


# ── Section parsers ───────────────────────────────────────────────────────────

def parse_works(tex):
    items = []
    for args in all_commands(tex, 'work', 6):
        role, company_raw, location, dates, desc_raw, tags_raw = args
        items.append({
            'role':             role.strip(),
            'company_html':     to_html(company_raw),
            'location':         to_text(location),
            'dates':            to_text(dates),
            'description_html': to_html(desc_raw),
            'tags':             [t.strip() for t in tags_raw.split(',') if t.strip()],
        })
    return items


def parse_projects(tex):
    items = []
    for args in all_commands(tex, 'project', 5):
        name, company_raw, year, desc_raw, tags_raw = args
        items.append({
            'name':             to_text(name),
            'company_html':     to_html(company_raw),
            'year':             year.strip(),
            'description_html': to_html(desc_raw),
            'tags':             [t.strip() for t in tags_raw.split(',') if t.strip()],
        })
    return items


def parse_education(tex):
    return [
        {'years': args[0].strip(), 'description_html': to_html(args[1])}
        for args in all_commands(tex, 'educationentry', 2)
    ]


def parse_references(tex):
    return [
        {
            'name':    args[0].strip(),
            'role':    args[1].strip(),
            'company': args[2].strip(),
            'period':  args[3].strip(),
            'email':   args[4].strip(),
        }
        for args in all_commands(tex, 'referee', 5)
    ]


def parse_skills(tex):
    return [
        {'category': to_text(args[0]), 'values': to_text(args[1])}
        for args in all_commands(tex, 'keywordsentry', 2)
    ]


# ── Entry point ───────────────────────────────────────────────────────────────

def build():
    main = read('cv.tex')
    first, last = get_2args(main, 'name')
    tagline     = get_arg(main, 'tagline')
    social_raw  = get_arg(main, 'socialinfo')

    data = {
        'name':    {'first': first.title(), 'last': last.title()},
        'tagline': tagline,
        'photo':   'photo.jpg',
        'social':  parse_social(social_raw),
        'sections': [
            {
                'id': 'summary', 'title': 'Summary', 'type': 'summary',
                'html': to_html(get_arg(read('sections/headline.tex'), 'summaryText')),
            },
            {
                'id': 'experience', 'title': 'Experience', 'type': 'works',
                'items': parse_works(read('sections/experience.tex')),
            },
            {
                'id': 'skills', 'title': 'Skills &amp; Abilities', 'type': 'skills',
                'items': parse_skills(read('sections/skills.tex')),
            },
            {
                'id': 'projects', 'title': 'Personal Projects', 'type': 'projects',
                'items': parse_projects(read('sections/personal_projects.tex')),
            },
            {
                'id': 'teaching', 'title': 'Teaching &amp; Mentoring', 'type': 'works',
                'items': parse_works(read('sections/teaching.tex')),
            },
            {
                'id': 'education', 'title': 'Education', 'type': 'education',
                'items': parse_education(read('sections/education.tex')),
            },
            {
                'id': 'references', 'title': 'References', 'type': 'references',
                'items': parse_references(read('sections/references.tex')),
            },
        ],
    }

    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    print(f'Wrote {OUT}')


if __name__ == '__main__':
    build()