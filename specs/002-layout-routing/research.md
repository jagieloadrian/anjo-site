# Phase 0 Research: Layout and Routing

All items below were resolved by reading this project's actual dependency sources (Kobweb
0.25.1's `kobweb-core-js` and `kobweb.gradle.application` sources jars), not from general Kobweb
knowledge, matching Phase 0's research methodology. No `NEEDS CLARIFICATION` markers remained
after `/speckit-clarify`, so this is confirmation research, not open-ended discovery.

## 1. Kobweb's file-based routing fully replaces the mock's router

**Decision**: No custom routing code is needed. A `@Composable` function annotated `@Page` in
`pages/` generates a route from its filename (PascalCase → kebab-case; `Index` is special-cased
to the parent directory's root), confirmed in `com/varabyte/kobweb/core/Page.kt`'s doc comment:
`"pages/account/Profile.kt" -> "/account/profile"`, `"pages/blog/Index.kt"` renders at `/blog`.
`Link("/about", "About")` (already used in `NavHeader.kt`) is a plain anchor-backed navigation —
no JS router, no hash routing.

**Rationale**: This is exactly what F005 asked to confirm. The mock's `app.js:78` comment ("swap
this for hash routing... on GitHub Pages") anticipated a problem Kobweb's static export already
solves — each route gets its own real, crawlable, linkable HTML file.

**Alternatives considered**: None — there is no scenario where a custom router would be needed on
top of Kobweb's own routing for a purely static site.

## 2. Generating an on-brand `404.html`

**Decision**: Create `pages/Error404.kt` with `@Page("/404")`, rendering the same `PageLayout`
(so it inherits nav, footer, and the Phase 0 visual foundation) with a minimal "not found"
message.

**Rationale**: Read directly from `com/varabyte/kobweb/gradle/application/extensions/AppBlock.kt`'s
`RouteConfig` class: a route's default export path is `route + ".html"` unless the route ends in
`/` (which maps to `index.html`). A route of `/404` therefore exports to `404.html` at the site
root — exactly the filename GitHub Pages looks for on any unmatched path. No special Kobweb
"error page" API exists (confirmed: no `NotFound`/`404`-related type anywhere in `kobweb-core` or
the application Gradle plugin) — this is a plain `@Page` like any other, just placed at the
`/404` route via `routeOverride`.

**Alternatives considered**:
- Relying on Kobweb to auto-generate a 404 page — rejected, confirmed no such mechanism exists;
  `KobwebExportTask.kt:246` only warns if there's no *root* (`/`) route, unrelated to 404 handling.
- A file literally named `404.kt` — rejected; Kotlin file names backing a `@Composable` need a
  valid function identifier inside, and `@Page("/404")`'s explicit `routeOverride` (leading `/`
  makes it an absolute path per `Page.kt`'s doc) is simpler and matches the doc's own recommended
  pattern for a route that shouldn't be derived from the filename.

## 3. Language-state mechanism (CompositionLocal, no new dependency)

**Decision**: A `staticCompositionLocalOf<Lang>` (new file `Lang.kt`), provided at the
`AppEntry.kt` app-shell level (same place Phase 0 added the overlay `Div`s), initialized once
from `kotlinx.browser.window.navigator.language`, and updated via a `mutableStateOf` held by the
provider so changing it recomposes every consumer.

**Rationale**: `androidx.compose.runtime` (already a declared dependency, `compose.runtime`) is
the same library Kobweb/Compose HTML is built on — `CompositionLocal` is its standard mechanism
for app-wide state every composable can read without prop-drilling, and it requires zero new
Gradle dependencies (constitution Principle VIII). This mirrors the existing pattern
`AppEntry.kt` already uses for `ColorMode` (Silk's own `CompositionLocal`-backed `ColorMode.current`)
— same shape, new instance, for a second orthogonal piece of shared state.

**Alternatives considered**:
- A `Flow`/`StateFlow`-based service — rejected, unnecessary machinery for in-memory,
  single-tab, non-persisted state (YAGNI).
- Reusing `ColorMode`'s own storage/provider pattern more literally (a `LangMode` enum with
  `.current`/`.currentState` extension functions mimicking Silk's `ColorMode`) — considered, but
  `ColorMode`'s machinery lives in Silk internals not meant for reuse outside color theming; a
  plain `CompositionLocal` is the smaller, more standard diff for a one-off need.

**Implementation addendum (discovered during `/speckit-implement`, analyze finding F1)**: a single
read-write `CompositionLocal<MutableState<Lang>>` was considered, but a plain `Lang` value is
simpler for every consumer to read (`LocalLang.current`, no `.value` unwrapping). So the mutation
path was split into a second, paired `CompositionLocal`: `LocalLangSetter: ProvidableCompositionLocal<(Lang) -> Unit>`,
provided alongside `LocalLang` at the same `AppEntry.kt` call site. Consumers that only read
(`Index.kt`, `Error404.kt`, placeholder pages, `NavHeader.kt`'s nav labels) use `LocalLang.current`;
only the language switch button (`NavHeader.kt`'s `LangSwitchButton`) uses `LocalLangSetter.current`
to flip it. Both locals are provided exactly once (data-model.md's validation rule extends to both).

## 4. Browser language detection

**Decision**: Read `kotlinx.browser.window.navigator.language` (a `String` like `"pl-PL"` or
`"en-US"`) once, at `Lang` initialization time; default to `Lang.PL` if it starts with `"pl"`
(case-insensitive), else `Lang.EN`.

**Rationale**: `kotlinx.browser` is already a transitive dependency — `AppEntry.kt` already
imports `kotlinx.browser.document` from it for the app's `<title>`/local-storage logic — so
`kotlinx.browser.window` costs nothing new. `navigator.language` is the standard, no-permission,
synchronous browser API for this; no async Promise/geolocation API is involved (keeps this
static-safe per constitution Principle I).

**Alternatives considered**:
- `navigator.languages` (full ordered list) — rejected as unnecessary precision for a two-language
  site; the first/primary `navigator.language` value is sufficient per spec's resolved
  Clarification.
- Persisting the detected/chosen language to `localStorage` (mirroring `ColorMode`'s own
  `loadFromLocalStorage`/`saveToLocalStorage` pattern) — explicitly deferred per spec's Edge Cases
  ("cross-page/session persistence is not required until real pages with real copy exist,
  Phase 3") — not implemented this phase.

## 5. Placeholder pages for Projects/Trophies/Contact/CV

**Decision**: Four new files under `pages/` (`Projects.kt`, `Trophies.kt`, `Contact.kt`,
`Cv.kt`), each following the exact shape Phase 0 established for `Index.kt`'s placeholder:
`@Page` + `@Layout(".components.layouts.PageLayout")`, rendering only technical labels (e.g. the
page's own name) with zero real copy and zero interactive elements beyond what `PageLayout`
already provides (nav/footer).

**Rationale**: Reuses an already-established, already-constitution-compliant pattern (Phase 0's
D1 fix already proved Index-style placeholders satisfy Principle III's bilingual-parity carve-out
for technical labels) rather than inventing a new placeholder shape. `Cv.kt` is named with
Kobweb's PascalCase-to-kebab-case rule in mind — confirmed via `Page.kt`'s doc comment logic, a
file named `Cv.kt` yields route `/cv` (not `/c-v`), since kebab-casing only inserts hyphens at
lowercase-to-uppercase boundaries and "Cv" has none after the first letter.

**Alternatives considered**:
- Building real page content now — rejected, explicitly out of scope (spec Assumptions, Phase 3's
  responsibility).
- Skipping placeholder pages and instead disabling those four nav links until Phase 3 — rejected
  per the resolved Clarification (all six links must be real, non-dead destinations this phase).

## 6. Scoping the reduced-motion override to `:hover` specifically (discovered during implementation)

**Decision**: Silk's `CssStyleScope` (used inside a `CssStyle { ... }` block) exposes pseudo-class
selectors as composable `CssRule` values — `val StyleScope.hover` (from
`com.varabyte.kobweb.silk.style.selectors`) plus an `operator fun CssRule.invoke(createModifier)`
that lets you write `hover { Modifier... }` directly. Media queries compose the same way:
`CssRule.OfMedia(mediaQuery) + hover` produces a rule scoped to `:hover` *within* that media query
(`@media (...) { &:hover { ... } }`), not just the media query alone. `NavHeader.kt`'s `BrandStyle`
uses this to cancel only the hover-triggered glitch animation under
`prefers-reduced-motion: reduce`, via `Modifier.animation { name("none") }` (the
`AnimationScope.name(...)` longhand, which overrides just `animation-name` from the base
`:hover` rule's shorthand `animation: ...`).

**Rationale**: This mirrors Phase 0's `.fx-scan` reduced-motion pattern (a media-query-scoped
override) but composed with a pseudo-class instead of applied at the top level — needed because
only the *hover-triggered* animation should be cancelled, not some other always-on rule.

**Alternatives considered**: A raw CSS selector string (`registerStyle(".brand:hover")`  +
`cssRule(mediaQuery)`), matching Phase 0's `AppStyles.kt` pattern — works too, but `CssStyle`'s
typed `hover`/`CssRule.OfMedia` composition is the same mechanism already used for
`SideMenuSlideInAnim`'s surrounding `CssStyle`, so it was kept in-family with `NavHeader.kt`'s
existing style, rather than moving this style into `AppStyles.kt`'s raw-selector world.
