Build the local site directory and start a development server on port 8080.

Run these commands from the project root:

1. Prepare the _site directory (mirrors CI):
```
rm -rf /Users/ttaaa/Projects/Personal/CV/_site && cp -r /Users/ttaaa/Projects/Personal/CV/web /Users/ttaaa/Projects/Personal/CV/_site && cp /Users/ttaaa/Projects/Personal/CV/cv.pdf /Users/ttaaa/Projects/Personal/CV/_site/cv.pdf && cp /Users/ttaaa/Projects/Personal/CV/latex/photo.jpg /Users/ttaaa/Projects/Personal/CV/_site/photo.jpg
```

2. Free the port if already in use:
```
lsof -ti tcp:8080 | xargs kill -9 2>/dev/null; sleep 1
```

3. Start the server in the background:
```
cd /Users/ttaaa/Projects/Personal/CV/_site && python3 -m http.server 8080
```

Tell the user the site is available at http://localhost:8080 and the server is running in the background.