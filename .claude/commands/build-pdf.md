Generate the CV artifacts from the Kotlin DSL, then compile the PDF. All artifacts go to the `build/` directory.

Run these commands sequentially:

1. Generate LaTeX sources (build/latex) and portfolio data (build/cv-data.json) from the Kotlin DSL:
```
cd /Users/ttaaa/Projects/Personal/CV && ./gradlew run
```

2. Compile with LuaLaTeX from build/latex so the local class file resolves (run twice for stable output):
```
cd /Users/ttaaa/Projects/Personal/CV/build/latex && /Library/TeX/texbin/lualatex -synctex=1 -interaction=nonstopmode -file-line-error -output-directory=.. cv.tex
```
Run it a second time to resolve cross-references:
```
cd /Users/ttaaa/Projects/Personal/CV/build/latex && /Library/TeX/texbin/lualatex -synctex=1 -interaction=nonstopmode -file-line-error -output-directory=.. cv.tex
```

Report success or any errors from the Gradle run or the LaTeX compilation log.
