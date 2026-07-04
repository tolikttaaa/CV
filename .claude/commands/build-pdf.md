Compile the CV LaTeX source to PDF and regenerate the portfolio JSON data. All artifacts go to the `build/` directory.

Run these commands sequentially:

1. Ensure the build directory exists:
```
mkdir -p /Users/ttaaa/Projects/Personal/CV/build
```

2. Compile with LuaLaTeX from the latex/ directory so the local class file resolves (run twice for stable output):
```
cd /Users/ttaaa/Projects/Personal/CV/latex && /Library/TeX/texbin/lualatex -synctex=1 -interaction=nonstopmode -file-line-error -output-directory=../build cv.tex
```
Run it a second time to resolve cross-references:
```
cd /Users/ttaaa/Projects/Personal/CV/latex && /Library/TeX/texbin/lualatex -synctex=1 -interaction=nonstopmode -file-line-error -output-directory=../build cv.tex
```

3. Regenerate portfolio data (writes build/cv-data.json):
```
python3 /Users/ttaaa/Projects/Personal/CV/scripts/parse_cv.py
```

Report success or any errors from the LaTeX compilation log.
