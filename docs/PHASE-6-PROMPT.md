# Input prompt for `/speckit-specify` — Phase 6 (Polish) + tests

Branch: `feature-7/tests-polishing` → spec folder `specs/007-tests-polishing`

```
Phase 6 — Polish: close out ROADMAP.md Faza 6 (F030-F033) — per-page SEO meta/OG tags
built on top of the existing base description.set() in site/build.gradle.kts, a full
accessibility pass (alt-text audit across all widget components, complete
prefers-reduced-motion coverage beyond the current Terminal/glitch-hover cases, contrast
check of SiteTheme.kt tokens against WCAG AA), and a go/no-go call on the two optional
items — privacy-friendly analytics (Plausible/GoatCounter) and self-hosted fonts instead
of the Google Fonts CDN — implement only if they clear YAGNI, otherwise record as
explicitly deferred rather than silently dropped.

Also introduce the project's first automated test suite beyond scripts/refresh-trophies
(branch: feature-7/tests-polishing). scripts/refresh-trophies/index.test.mjs already
covers the PSN-fetch mapping logic via node:test — reuse that pattern, don't reinvent it.
New scope:
- Kotlin/JS unit tests (jsTest source set, kotlin.test — no new Gradle dependency) for
  pure logic only: Lang.kt's browser-language detection and BilingualString /
  BilingualTimelineItem resolve(), the JSON-parsing/mapping functions in Trophies.kt and
  Projects.kt, and the slug lookup in pages/projects/Slug.kt (including the unknown-slug
  → /404 fallback).
- Playwright UI/browser/e2e suite (new devDependency, new e2e/ workspace at repo root —
  first browser-level automated coverage in the project) running against a real
  `kobwebExport --layout static` output served locally (reuse CI's existing site-export
  job in .github/workflows/ci.yml as the build step, add a serve+test step after it).
  Cover: all 7 static routes (Home/About/Projects/Trophies/Contact/CV/404) load without
  console errors, one dynamic /projects/{slug} detail page plus the unknown-slug → /404
  redirect, EN/PL lang toggle actually swaps visible text, prefers-reduced-motion=reduce
  disables the scanline/glitch effects, and the three F024a breakpoints (390/430/768)
  render without overflow/broken touch targets — this replaces the manual
  docs/handoff/mobile-check.html review with an automated one.
- Wire all test commands (existing node:test, new kotlin.test, new Playwright) into CI so
  they run on every push, not just locally.

Maps to ROADMAP.md F030-F033 (polish) and F034 (Kotlin/JS unit tests) + F035 (Playwright
E2E suite).
```
