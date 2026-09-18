# anjo-site

Adrian Jagieło's personal portfolio site. Built with [Kobweb](https://github.com/varabyte/kobweb)
(Kotlin/JS + Compose HTML + Silk), exported as a static site, deployed to GitHub Pages.

## Structure

- `site/` — the Kobweb application.
  - `src/jsMain/kotlin/com/anjo/anjosite/pages/` — routes: `Index`, `About`, `Projects` (+
    dynamic `projects/{slug}`), `Trophies`, `Contact`, `Cv`, `Error404`.
  - `src/jsMain/kotlin/com/anjo/anjosite/styles/` — CSS as Kotlin `StyleSheet()` objects, one
    file per concern, aggregated in `SiteStyles.kt`.
  - `src/jsMain/resources/public/` — static assets: fonts, `projects.json`, `trophies.json`,
    `stack.json`.
  - `src/jsTest/kotlin/` — `kotlin.test` unit tests for pure logic (JSON parsing, slug lookup).
- `e2e/` — Playwright browser test suite, run against a real static export.
- `scripts/refresh-trophies/` — Node script that fetches PSN trophy data via `psn-api` and writes
  `trophies.json`; covered by `node:test`.
- `.github/workflows/` — CI (`ci.yml`), GitHub Pages deploy (`deploy.yml`), and a nightly trophy
  refresh (`trophies.yml`, `npsso-reminder.yml`).

## Getting started

```bash
cd site
./gradlew kobwebStart
```

Open [http://localhost:8080](http://localhost:8080). Stop with `./gradlew kobwebStop` or `Q` in
the Kobweb terminal UI.

## Testing

```bash
# Kotlin/JS unit tests
./gradlew :site:jsTest

# refresh-trophies script tests
cd scripts/refresh-trophies && npm test

# Playwright e2e suite (needs a static export first)
./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC
cd e2e && npm install && npx playwright install --with-deps chromium && npx playwright test
```

## Exporting

```bash
./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC
```

Produces a static site at `site/.kobweb/site/`, deployable to any static host. CI exports and
deploys this to GitHub Pages on push to `main`.
