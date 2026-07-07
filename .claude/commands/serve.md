Build the local site directory and start a development server on port 8080.

Run:

```
cd /Users/ttaaa/Projects/Personal/CV && ./gradlew serveSite
```

This runs the whole pipeline (`generateLatex` → `generatePdf`, `generateWeb` → `assembleSite`) and starts a detached `python3 -m http.server 8080` in build/site.

Tell the user the site is available at http://localhost:8080. To stop the server: `./gradlew stopSite`.
