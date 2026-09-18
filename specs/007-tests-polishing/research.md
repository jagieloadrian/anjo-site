# Phase 0 Research: Polish + Automated Test Suite

## §1. Kotlin/JS unit testing — how to add `kotlin.test` with zero new Gradle dependencies

**Decision**: Add a `jsTest` block under `kotlin { sourceSets { ... } }` in `site/build.gradle.kts`
with `implementation(kotlin("test"))`.

**Rationale**: `kotlin("test")` (the `org.jetbrains.kotlin:kotlin-test` artifact family) is
resolved through the Kotlin Gradle plugin already applied (`libs.plugins.kotlin.multiplatform`,
version 2.4.10 per `gradle/libs.versions.toml`) — it needs no new `[libraries]` entry in the
version catalog, just the `kotlin("test")` shorthand, which Kotlin Multiplatform's `jsTest`
source set understands to mean "the JS test variant of kotlin-test" automatically. `./gradlew
:site:jsTest` becomes the run command (or `:site:allTests`, Kotlin MPP's aggregate task).

**Alternatives considered**: Kotest — rejected, a heavier, opinionated framework for a handful of
pure-function tests (Constitution Principle VIII). JUnit directly — not applicable, this is a
Kotlin/JS-only module (no `jvmMain`), and `kotlin.test` already provides the right JS test runner
(Karma/Mocha under the hood via the Kotlin Gradle plugin) with zero extra configuration.

## §1b. `Lang.kt`'s `detectInitialLang()` reads a real browser global — not directly testable

**Decision**: Extract `internal fun langForLocale(locale: String): Lang` (the pure `pl`-prefix
rule) from `detectInitialLang()`'s body; `detectInitialLang()` becomes a thin wrapper:
`langForLocale(window.navigator.language)`.

**Rationale**: Found while drafting `/speckit-tasks` — Kotlin Multiplatform's `jsTest` for a
`browser()` JS target (what `configAsKobwebApplication` configures) runs via Karma in a real
(headless) browser, so `window.navigator.language` reflects that browser/CI runner's actual
locale, not a value a `kotlin.test` case can set per-scenario. The same extraction pattern as §3's
`findProjectBySlug` — pull the pure decision out from behind the browser global — makes the rule
itself (not the glue that reads the real API) directly testable, with zero behavior change at the
real call site.

## §2. `Trophies.kt`'s `parseTrophiesData` is `private` — not visible to `jsTest`

**Decision**: Change `private fun parseTrophiesData(text: String): TrophiesData` to `internal
fun parseTrophiesData(...)`.

**Rationale**: Verified by reading `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Trophies.kt`
directly — the function is `private`, meaning file-private in Kotlin, invisible even to another
file in the same package (`com.anjo.anjosite.pages`), let alone a different source set. Kotlin
Multiplatform's `jsTest` source set can see `internal` declarations of the `jsMain` source set it
tests (same module, same effective visibility boundary as `internal` always has) — the smallest
possible visibility widening that makes FR-009 testable, with no behavior change and no new
public API surface. `TrophiesData`/`TrophiesStat` (the function's parameter/return types) are
already top-level `data class`es with default (public) visibility, so no further change is
needed there.

**Alternatives considered**: Testing only through the full page composable (`TrophiesPage()`) —
rejected, that requires a Compose test runtime and a real/mocked `window.fetch`, testing far more
than the pure parsing logic FR-009 actually asks for, and reintroducing exactly the
flaky-integration-test risk the spec's Edge Cases section warns against. Duplicating the parsing
logic into a test-only copy — rejected, guarantees the test drifts from the real implementation.

## §3. `pages/projects/Slug.kt`'s slug lookup is inline in a `@Composable` — not unit-testable as-is

**Decision**: Extract the lookup into a small, pure, `internal` function:

```kotlin
internal fun findProjectBySlug(entries: List<ProjectEntry>, slug: String?): ProjectEntry? =
    entries.find { it.slug == slug }
```

placed in `Slug.kt` above `SlugPage()`, which then calls it instead of the inline
`entries.find { ... }`.

**Rationale**: Read `Slug.kt` directly — today the lookup is a one-line inline expression inside
`SlugPage()`, entangled with `rememberPageContext()`, a `LaunchedEffect` network fetch, and
`ctx.router.navigateTo("/404")`. None of that is callable from a `kotlin.test` unit test without
a Compose runtime and a mocked router/fetch. Pulling the pure `List<ProjectEntry> + String? ->
ProjectEntry?` lookup into its own named function is the smallest possible change that makes
FR-010 (including the unknown-slug → `null` case, which `SlugPage()` already turns into the
`/404` redirect) directly testable, and is a legitimate, narrow refactor — not a redesign of the
page.

**Alternatives considered**: Testing the 404 redirect end-to-end only via Playwright (FR-012
already does this) — insufficient on its own per FR-010, which specifically asks for a *unit*-
level guarantee on the lookup itself, independent of routing/rendering.

## §4. Does a client-side (`LaunchedEffect`) `<head>` mutation survive Kobweb's static export? — verified empirically

**Decision**: Yes — extend the exact same `PageLayoutData` + `LaunchedEffect` pattern
`PageLayout.kt` already uses for `document.title`, via one shared `updatePageMeta(...)` helper
(data-model.md) that also creates/updates `<meta name="description">` and `<meta property="og:*">`
elements. `Slug.kt` calls the same helper a second time, once its own fetch resolves, since a
project's real title/description/image can't be known at static `@InitRoute` time (data-model.md).

**Rationale**: Ran `./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC` locally and
inspected the output HTML directly. `PageLayout.kt` sets `document.title` via a `LaunchedEffect`
keyed on `PageLayoutData.title` — pure client-side JS, executed after hydration. The exported
`site/.kobweb/site/about.html`, `trophies.html`, `404.html`, `index.html` each contain a distinct,
correct `<title>Adrian Jagieło — About</title>` (etc.) — proof that Kobweb's static export does a
real, post-JS-execution DOM snapshot per route ("Snapshotting ..." log output), not a build-time-
only template render. This means the identical technique — creating `<meta>` elements in that
same `LaunchedEffect` — will be captured by the export the same way, with no second, parallel,
build-time-only mechanism needed. The current `<meta name="description" content="Powered by
Kobweb">` (from `build.gradle.kts`'s global `description.set()`) is confirmed identical across
every exported page today — exactly the gap FR-001 closes.

**Alternatives considered**: Kobweb's `kobweb.app.index.head { }` build-time DSL (already used
for fonts/favicons) — rejected for per-page content specifically because it's evaluated once at
build time for the single shared `index.html` template, with no access to per-route data; it
remains the right place for the truly global, page-independent `<head>` entries (favicons, font
links) it already holds.

## §5. Serving the static export for Playwright — zero new dependency

**Decision**: `playwright.config.ts`'s `webServer.command` runs `python3 serve-static.py <port>
../site/.kobweb/site` — `e2e/serve-static.py`, a ~20-line `http.server.SimpleHTTPRequestHandler`
subclass (pure stdlib, still zero new dependency) that falls back to `404.html` (404 status) for
any path with no matching file, mirroring GitHub Pages' real unmatched-path behavior (the same
behavior `Error404.kt`'s own comment documents). A bare `python3 -m http.server` has no such
fallback, so a client-side-only route like `/projects/{unknown-slug}` (FR-012, only 4 slugs are
pre-registered via `addExtraRoute`) would hit a plain HTTP 404 with no HTML/JS at all instead of
booting the real app and letting its client-side router redirect to `/404`.

**Rationale**: `ubuntu-latest` GitHub Actions runners ship Python 3 preinstalled, and so does
virtually every maintainer dev machine — a static file server with zero new npm dependency,
consistent with Constitution Principle VIII and the spec's explicit framing that Playwright/
axe-core are "the new dependencies" (singular concern), not a third serving tool on top.

**Alternatives considered**: `npx serve`/`http-server` (npm) — rejected, an avoidable new
dependency for a one-line stdlib-equivalent need. Kobweb's own dev server (`kobweb run`) —
rejected outright: Constitution Principle VII and spec Assumptions are explicit that "real static
export," not the dev server, is what must be tested. Plain `python3 -m http.server` with no
fallback — rejected once T022's unknown-slug test made the gap concrete (see above).

## §6/§7. `axe-core` ruleset and console-error threshold — already settled in `/speckit-clarify`

**Decision**: `axe-core` scans pass `{ runOnly: { type: "tag", values: ["wcag2a", "wcag2aa"] } }`
to `@axe-core/playwright`'s `.withTags(...)` builder. The route/console check listens only to the
Playwright `page.on("pageerror", ...)` (uncaught exceptions) and `page.on("console", msg =>
msg.type() === "error" ? fail : ignore)` events — `"warning"`-type console messages are not
asserted on.

**Rationale**: Both were resolved directly with the maintainer during `/speckit-clarify`
(spec.md's Clarifications section) — recorded here only so `/speckit-tasks` has the concrete API
shape, not re-litigated.

## §8. `og:image` fallback banner — one new static asset, sourced from existing branding

**Decision**: A single new `site/src/jsMain/resources/public/og-banner.png` (1200×630, the
Open Graph-recommended aspect ratio), built from the site's existing token palette
(`SiteTokenStyles.kt`'s `--bg`/`--ink`/`--pink`/`--cyan`) and the same "Adrian Jagieło" wordmark
already used in `PageLayout.kt`'s `document.title`, rather than a per-page-generated image.

**Rationale**: FR-002 (per the clarify session) only requires *one* site-wide fallback for every
page that isn't a project detail page (which already has `coverImageUrl`) — a single static
asset is the minimum that satisfies it, consistent with Principle VIII.

**Tooling (found during `/speckit-analyze`, U1)**: the file is hand-authored/maintainer-supplied
(any image editor, or a one-off export from a design tool) and committed as a plain static asset
— no image-generation library (Pillow, `sharp`, `cairosvg`, etc.) is added to the repo. This is a
deliberate choice, not an oversight: the repo has no existing raster-generation tooling in either
its Gradle or npm dependency graph, and adding one only to render a single static banner once
would need its own Constitution Principle VIII justification alongside Playwright/`axe-core`
(plan.md Constitution Check) — unnecessary for a one-time, no-code asset.

**Alternatives considered**: Generating a distinct OG image per non-project page (Home, About,
Projects grid, Trophies, Contact, CV, 404) — rejected by the clarify session itself as
unnecessary design work beyond this phase's polish/testing scope. A scripted/programmatic render
(e.g. an SVG built from `SiteTokenStyles.kt`'s tokens, rasterized in CI) — rejected: it would add
a new image-processing dependency for a single static file that changes essentially never, the
kind of one-off Principle VIII can't justify.

## §9. CI wiring

**Decision**: Extend `.github/workflows/ci.yml`'s existing `site-export` job with a `jsTest`
step (`./gradlew :site:jsTest`) before or alongside the export step, and add one new job,
`e2e`, that depends on a built static export (either re-running `kobwebExport` in that job, or
consuming the `site-export` job's output via `actions/upload-artifact`/`download-artifact` —
left for `/speckit-tasks` to pick, both are standard, zero-new-dependency GitHub Actions
patterns) and then runs `npm ci && npx playwright install --with-deps chromium && npx playwright
test` inside `e2e/`.

**Rationale**: Reuses the existing job/workflow file (FR-016) instead of a parallel CI system;
`refresh-trophies-tests` (the existing `node:test` job) is untouched, keeping the three test
layers (§ Technical Context in plan.md) independently runnable and independently attributable
when one fails.

## §10. FR-006/FR-007 go/no-go decision (per the clarify session, implementation is in scope if "now")

**Decision — Self-hosted fonts (FR-007): implement now.** Vendor the exact weights already
loaded (Archivo 400/500/600/700/800/900, JetBrains Mono 400/500/700) as `.woff2` files under
`site/src/jsMain/resources/public/fonts/`, add `@font-face` rules pointing at them, and remove
the `fonts.googleapis.com`/`fonts.gstatic.com` `<link>`/`<preconnect>` entries from
`build.gradle.kts`'s `head.add` block.

**Rationale**: Zero new dependency (static files only); both fonts are open-licensed (SIL Open
Font License) and already fully specified (exact weights, no variable-font ambiguity); the only
consumers of the font tokens are `--sans`/`--mono` custom properties in `SiteTokenStyles.kt`,
already single-sourced (Principle IV) — swapping their `@font-face` `src` is a localized,
low-blast-radius change. It closes a real, currently-live privacy gap (every visitor's IP is
requested against Google Fonts' CDN on every page load) with low, one-time cost — a good fit for
a phase that's already tightening accessibility/privacy posture.

**Decision — Privacy-friendly analytics (FR-006): deferred.** No code changes.

**Rationale**: Unlike fonts, this adds an ongoing third-party dependency: a hosted script
(Plausible or GoatCounter) plus an external account/dashboard the maintainer would need to create
and maintain outside this repository's own state — real, recurring complexity for a personal
portfolio site with no stated traffic/analytics goal anywhere in ROADMAP.md or in this session.
Constitution Principle VIII's own test — "no new library unless [it] genuinely can't be done in a
reasonable amount of code" — cuts the other way here: the honest answer is nothing in this phase
*needs* analytics; it's opportunistic, not required. Deferred, not rejected — trivial to revisit
(a single `<script>` tag) the moment a concrete need (e.g. "how many recruiters visit my CV
page") actually shows up.

**Note for the maintainer**: both calls above are this plan's recommendation, not a decision the
maintainer was asked to confirm directly (the `/speckit-clarify` question quota was spent on
higher-uncertainty items). Say so now if either call should flip before `/speckit-tasks` locks in
concrete font-vendoring tasks.
