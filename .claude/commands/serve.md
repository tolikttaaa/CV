Build the local site directory and start a development server on port 8080.

Run these commands from the project root:

1. Assemble the site in build/site (mirrors CI). Requires a prior /build-pdf run so build/cv.pdf and build/cv-data.json exist:
```
rm -rf /Users/ttaaa/Projects/Personal/CV/build/site && cp -r /Users/ttaaa/Projects/Personal/CV/web /Users/ttaaa/Projects/Personal/CV/build/site && cp /Users/ttaaa/Projects/Personal/CV/build/cv.pdf /Users/ttaaa/Projects/Personal/CV/build/site/cv.pdf && cp /Users/ttaaa/Projects/Personal/CV/build/cv-data.json /Users/ttaaa/Projects/Personal/CV/build/site/cv-data.json && cp /Users/ttaaa/Projects/Personal/CV/build/latex/photo.jpg /Users/ttaaa/Projects/Personal/CV/build/site/photo.jpg
```

2. Free the port if already in use:
```
lsof -ti tcp:8080 | xargs kill -9 2>/dev/null; sleep 1
```

3. Start the server in the background:
```
cd /Users/ttaaa/Projects/Personal/CV/build/site && python3 -m http.server 8080
```

Tell the user the site is available at http://localhost:8080 and the server is running in the background.
