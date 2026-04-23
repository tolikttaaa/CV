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
└── .github/workflows/
    └── build-deploy.yml        # Compile → deploy pipeline
```

## Building locally

Requires LuaLaTeX (TeX Live 2022+).

```sh
cd latex
lualatex cv.tex
```

The compiled PDF is written to `latex/cv.pdf`.

## Deployment

Every push to `main` triggers the GitHub Actions workflow:

1. Compiles `latex/cv.tex` with LuaLaTeX inside a full TeX Live container
2. Pushes `web/` contents + compiled `cv.pdf` to the `gh-pages` branch

GitHub Pages serves the result from the `gh-pages` branch (`/ root`).
