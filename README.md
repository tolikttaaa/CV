# Anatolii Anishchenko — CV

LaTeX CV with automatic compilation and deployment to GitHub Pages via GitHub Actions.

## Structure

```
├── latex/
│   ├── cv.tex                  # Root document
│   ├── documentMETADATA.cls    # Custom document class
│   ├── photo.jpg               # Profile photo
│   ├── fonts/                  # Source Sans Pro (local OTF files)
│   └── sections/
│       ├── headline.tex
│       ├── experience.tex
│       ├── skills.tex
│       ├── personal_projects.tex
│       ├── teaching.tex
│       ├── education.tex
│       └── references.tex
├── web/
│   ├── index.html              # GitHub Pages landing page
│   └── favicon.png             # Site icon
├── scripts/
│   └── parse_cv.py             # LaTeX → build/cv-data.json portfolio data
├── build/                      # All build output (gitignored)
│   ├── cv.pdf                  # Compiled CV + LaTeX artifacts
│   ├── cv-data.json            # Generated portfolio data
│   └── site/                   # Assembled site (deployed to Pages)
└── .github/workflows/
    └── build-deploy.yml        # Compile → deploy pipeline
```

## Building locally

Requires LuaLaTeX (TeX Live 2022+).

```sh
mkdir -p build
(cd latex && lualatex -output-directory=../build cv.tex)
python3 scripts/parse_cv.py
```

The compiled PDF and all other artifacts are written to `build/`.

## Deployment

Every push to `main` triggers the GitHub Actions workflow:

1. Compiles `latex/cv.tex` with LuaLaTeX inside a full TeX Live container
2. Generates portfolio data and assembles the site into `build/site`
3. Pushes `build/site` to the `gh-pages` branch

GitHub Pages serves the result from the `gh-pages` branch (`/ root`).
