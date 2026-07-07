Generate the CV artifacts from the Kotlin DSL and compile the PDF. All artifacts go to the `build/` directory.

Run:

```
cd /Users/ttaaa/Projects/Personal/CV && ./gradlew generatePdf
```

This runs the `generateLatex` task (Kotlin DSL → build/latex) and compiles with two LuaLaTeX passes → build/cv.pdf. The LuaLaTeX output is captured in build/lualatex.log.

To also regenerate the web data, run `./gradlew generateWeb`, or `./gradlew run` for both.

Report success or any errors (check build/lualatex.log on compilation failure).
