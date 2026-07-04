Compile the CV LaTeX source to PDF and regenerate the portfolio JSON data.

Run these commands from the project root sequentially:

1. Compile with LuaLaTeX (run twice for stable output):
```
/Library/TeX/texbin/lualatex -synctex=1 -interaction=nonstopmode -file-line-error -output-directory=/Users/ttaaa/Projects/Personal/CV /Users/ttaaa/Projects/Personal/CV/latex/cv.tex
```
Run it a second time to resolve cross-references:
```
/Library/TeX/texbin/lualatex -synctex=1 -interaction=nonstopmode -file-line-error -output-directory=/Users/ttaaa/Projects/Personal/CV /Users/ttaaa/Projects/Personal/CV/latex/cv.tex
```

2. Regenerate portfolio data:
```
python3 /Users/ttaaa/Projects/Personal/CV/scripts/parse_cv.py
```

Report success or any errors from the LaTeX compilation log.
