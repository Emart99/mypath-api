#!/usr/bin/env python3
"""Strip // and /* ... */ comments from .java files under src/, dry-run by default."""
import argparse
import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent / "src"


def strip_comments(text: str) -> tuple[str, int]:
    out = []
    i, n = 0, len(text)
    state = "normal"
    removed = 0
    while i < n:
        c = text[i]
        nxt = text[i + 1] if i + 1 < n else ""

        if state == "normal":
            if c == '"' and text[i:i + 3] == '"""':
                state = "in_text_block"
                out.append('"""')
                i += 3
                continue
            elif c == '"':
                state = "in_string"
                out.append(c)
            elif c == "'":
                state = "in_char"
                out.append(c)
            elif c == "/" and nxt == "/":
                state = "in_line_comment"
                removed += 1
                i += 2
                continue
            elif c == "/" and nxt == "*":
                state = "in_block_comment"
                removed += 1
                i += 2
                continue
            else:
                out.append(c)

        elif state == "in_string":
            out.append(c)
            if c == "\\":
                if nxt:
                    out.append(nxt)
                    i += 2
                    continue
            elif c == '"':
                state = "normal"

        elif state == "in_char":
            out.append(c)
            if c == "\\":
                if nxt:
                    out.append(nxt)
                    i += 2
                    continue
            elif c == "'":
                state = "normal"

        elif state == "in_text_block":
            if text[i:i + 3] == '"""':
                out.append('"""')
                state = "normal"
                i += 3
                continue
            out.append(c)

        elif state == "in_line_comment":
            if c == "\n":
                out.append(c)
                state = "normal"

        elif state == "in_block_comment":
            if c == "*" and nxt == "/":
                state = "normal"
                i += 2
                continue
            elif c == "\n":
                out.append(c)

        i += 1

    return "".join(out), removed


def self_check():
    src, n = strip_comments('int x = 1; // hi\n')
    assert src == "int x = 1; \n" and n == 1

    src, n = strip_comments('/* block\ncomment */int y = 2;')
    assert src == "\nint y = 2;" and n == 1

    src, n = strip_comments('String url = "http://example.com"; // note\n')
    assert src == 'String url = "http://example.com"; \n' and n == 1

    src, n = strip_comments("char c = '/';\n")
    assert src == "char c = '/';\n" and n == 0

    src, n = strip_comments('.content("""\n        {"url":"https://example.com"}""")); // note\n')
    assert src == '.content("""\n        {"url":"https://example.com"}""")); \n' and n == 1

    print("self-check passed")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true", help="write changes (default is dry-run)")
    parser.add_argument("files", nargs="*", help="specific .java files to process (default: all under src/)")
    args = parser.parse_args()

    files = sorted(pathlib.Path(p) for p in args.files) if args.files else sorted(ROOT.rglob("*.java"))
    total_removed = 0
    changed_files = 0

    for f in files:
        text = f.read_text(encoding="utf-8")
        new_text, removed = strip_comments(text)
        if removed == 0:
            continue
        changed_files += 1
        total_removed += removed
        print(f"{f}: {removed} comment(s)")
        if args.apply:
            f.write_text(new_text, encoding="utf-8")

    mode = "APPLIED" if args.apply else "DRY-RUN"
    print(f"\n[{mode}] {changed_files} file(s), {total_removed} comment(s) total")


if __name__ == "__main__":
    self_check()
    main()
