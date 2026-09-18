# Quickstart: Polish + Automated Test Suite

Validation guide for all four user stories. Unlike every prior phase, this one *is* mostly
automated — steps 1-2 below are what CI itself runs (FR-016); steps 3-4 are the handful of things
automation doesn't cover.

## Prerequisites

- JDK 21 + Gradle wrapper (already required by the repo).
- Node.js 20+ (already required for `scripts/refresh-trophies`).
- Python 3 (for serving the static export locally — already present on any normal dev machine
  and on `ubuntu-latest`; research.md §5).

## 1. Run the Kotlin unit tests (User Story 3, FR-008/FR-009/FR-010)

```bash
./gradlew :site:jsTest
```

**Expected outcome**: all tests in `site/src/jsTest/kotlin/` pass, covering `Lang.kt`'s language
detection, `Trophies.kt`/`Projects.kt`'s JSON parsing, and `Slug.kt`'s `findProjectBySlug`
(including the unknown-slug → `null` case).

## 2. Run the Playwright browser suite (User Story 2 & 3, FR-011…FR-017)

```bash
# Build the real static export the suite runs against (Constitution Principle VII)
./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC

cd e2e
npm install
npx playwright install --with-deps chromium
npx playwright test
```

**Expected outcome**: all specs pass —

- `routes.spec.ts`: all 7 routes + 404 load with no `console.error`/unhandled exception; the
  dynamic project page renders; an unknown slug redirects to `/404`.
- `lang-toggle.spec.ts`: toggling EN/PL changes visible text.
- `reduced-motion.spec.ts`: with `prefers-reduced-motion: reduce` emulated, no decorative
  animation is detected.
- `breakpoints.spec.ts`: at 390px/430px/768px, no horizontal overflow and no touch target under
  48px.
- `a11y.spec.ts`: `axe-core` (`wcag2a`+`wcag2aa` only) reports zero violations, in both light and
  dark color mode, on every page.

If any of these fail, that's the exact same failure a pull request would hit in CI (FR-016) — fix
before pushing, not after.

## 3. Spot-check SEO/OG output by hand (User Story 1)

```bash
grep -A1 '<title>\|og:title\|og:description\|og:image' site/.kobweb/site/trophies.html
grep -A1 '<title>\|og:title\|og:description\|og:image' site/.kobweb/site/projects/star-wars-wiki-compose.html
```

**Expected outcome**: each page shows its own distinct `<title>`/`og:title`/`og:description`; the
project page's `og:image` matches that project's `coverImageUrl` from `projects.json`, every
other page's `og:image` is `/og-banner.png`.

## 4. Confirm self-hosted fonts (research.md §10, FR-007's decision is implement-now)

```bash
grep -i "fonts.googleapis.com\|fonts.gstatic.com" site/build.gradle.kts
```

**Expected outcome**: no match — the `<link>`/`<preconnect>` entries are gone, replaced by local
`@font-face` rules. Open any exported page in a browser with devtools' Network tab open and
confirm no request to `fonts.googleapis.com`/`fonts.gstatic.com` fires.

## 5. Confirm CI wiring end-to-end

Open a pull request touching any file under `site/` or `e2e/`. **Expected outcome**: three green
checks — the existing `refresh-trophies-tests` job (untouched), the extended `site-export` job
(now also running `:site:jsTest`), and the new `e2e` job — all without a manual trigger.

## 6. Spot-check the CI time budget (SC-005)

On that same pull request's **Actions** tab, note the total wall-clock time from the run starting
to all three jobs finishing (GitHub Actions shows per-job and total durations).

**Expected outcome**: under 10 minutes. This is a one-time spot-check, not an enforced gate
(spec.md Assumptions: an arbitrary but reasonable default, not a maintainer-specified hard
number) — if it's over, that's a signal to revisit, not a blocking failure.
