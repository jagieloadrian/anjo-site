# Phase 0 Research: Pages

## 1. Project detail routing: Kobweb dynamic segment + static-export registration

**Decision**: Add a dynamic-route page file (e.g. `pages/projects/Slug.kt`) annotated
`@Page("{}")`, which Kobweb's file-based router registers as `/projects/{slug}` (per
`Page.kt`'s doc comment: `@Page("{}")` on a file named `Slug.kt` under `pages/projects/`
yields `/projects/{slug}`). Inside the page, read the segment via
`rememberPageContext().route.params["slug"]` and look it up in the in-source projects list
(FR-006). Critically, **also register one concrete `RouteConfig` per project slug in
`site/build.gradle.kts`'s `kobweb { app { export { addExtraRoute(...) } } }` block** (the
`extraRoutes` property itself is `internal`; `addExtraRoute(route, exportPath?)` is the public API
a build script actually calls) — the
export task's route discovery explicitly *skips* any route containing `{` ("Skip export routes
with dynamic parts, as they are dynamically generated based on their URL anyway",
`KobwebExportTask.kt`), so without `extraRoutes`, `kobweb export --layout static` would produce
zero HTML files for project details, breaking SC-006 and the whole point of Q1's clarification
(a real, shareable, bookmarkable URL — GitHub Pages has no server to resolve an unregistered
path at request time, unlike a dev server).

**Rationale**: This is the only mechanism Kobweb's static-export pipeline offers for turning a
dynamic route into real static files (there is no per-page "generate static paths" hook the way
some SSG frameworks provide) — `extraRoutes` is a small, explicit list the build script owns.

**Alternatives considered**: Client-side-only detail view within `/projects` (no dynamic route)
— rejected in clarification Q1. Server-rendering the fullstack layout instead of static export —
rejected, violates constitution Principle VII (GitHub Pages static export is the sole deployment
target).

**Task-breakdown note**: the small list of project slugs is a plain `List<String>` literal
duplicated once in `build.gradle.kts`'s `extraRoutes` next to a comment pointing at the real data
source — for a personal-portfolio scale (a handful of projects) this is the smallest working
solution; if the project count ever grows large enough for manual sync to become error-prone, the
upgrade path is generating that list from a shared file both the build script and the page can
read.

## 2. Unknown/missing project slug

**Decision**: If `route.params["slug"]` doesn't match any entry in the projects list, call
`rememberPageContext().router.navigateTo("/404")` — reusing the existing Phase 1 404 route
(`Error404.kt`) rather than building a second not-found UI, per spec Assumptions.

**Rationale**: `Router.navigateTo(...)` is the existing public navigation API (already implicitly
used by every `Link` in the codebase); no new mechanism needed.

## 3. `trophies.json` fetch, without a new dependency

**Decision**: Fetch via the browser-native `kotlinx.browser.window.fetch(url)` call (returns a
`Promise<Response>`), awaited with `kotlinx.coroutines`'s `Promise<T>.await()` extension inside a
`LaunchedEffect` — `kotlinx-coroutines-core` is already a transitive dependency (`Terminal.kt`
already imports `kotlinx.coroutines.delay`). Model the fetch as a small sealed state
(`Loading` / `Loaded(TrophiesData)` / `Failed`) held in `remember { mutableStateOf(...) }`, driving
FR-009/FR-016's three render branches.

**Rationale**: No HTTP client library is declared or needed — the Fetch API is a browser
built-in accessible from Kotlin/JS's stdlib DOM bindings, and `kotlinx-coroutines`'s `await()` is
already on the classpath. Matches constitution Principle VIII (YAGNI on dependencies).

**Alternatives considered**: `XMLHttpRequest` — rejected, `fetch` is the modern browser-native API
and Kotlin/JS's `org.w3c.fetch` bindings already cover it. Adding Ktor client — rejected, would be
a new Gradle dependency for something one `fetch()` call already solves.

## 4. Parsing `trophies.json` without `kotlinx.serialization`

**Decision**: Parse the fetched response's text with Kotlin/JS's built-in `JSON.parse<dynamic>(...)`
and manually read fields into `TrophiesData`/`StatItem`/`GameCoverImage`/`TrophyEntry` instances,
rather than adding the `kotlinx.serialization` plugin/runtime.

**Rationale**: `kotlinx-serialization` is not currently declared anywhere in this project (no
plugin in `site/build.gradle.kts`, unlike, say, a typical Kobweb+Ktor fullstack template) — adding
it for one small, hand-authored JSON file is exactly the dependency creep constitution Principle
VIII forbids. `JSON.parse` + manual `dynamic` field access is a few lines and needs nothing new.

**Alternatives considered**: `kotlinx.serialization.json.Json.decodeFromString<T>()` — rejected,
new Gradle plugin + dependency for a problem five lines of manual parsing already solves at this
data size (a handful of games/trophies for one personal profile, not an arbitrary external API
response).

## 5. CV print-only chrome hiding

**Decision**: Use Silk's built-in `mediaPrint` `CssRule` (`StyleScope.mediaPrint`, backed by
`CSSMediaQuery.MediaType(CSSMediaQuery.MediaType.Enum.Print)`) on `NavHeaderStyle` and
`FooterStyle` to `display: none` both under `@media print`, site-wide.

**Rationale**: Satisfies FR-017/SC-004 ("no interactive site chrome... competing with the printed
content") with two one-line rule additions to already-existing styles — no per-route conditional
rendering needed, and hiding nav/footer when printing *any* page is a reasonable default anywhere
on a static personal site, not just `/cv`. `mediaPrint` is a ready-made Silk helper (no custom
media-query string needed, unlike the mock's 720px breakpoint in Phase 2 which had no matching
`Breakpoint` step). (FR-017 was added during the `/speckit-analyze` pass — this decision was
originally justified only by SC-004; it now has a direct requirement too.)

**Alternatives considered**: A `/cv`-only print stylesheet toggle — rejected as unnecessary
complexity; nothing about the requirement is CV-specific once expressed as "hide nav chrome when
printing."

## 6. `ContactPrompt`: the one new shared component (FR-014 exception)

**Decision**: Build `ContactPrompt` from Silk's own native form primitives — `TextInput(text,
onTextChange, ...)` for the message field and `Button(onClick) { ... }` for send — both already
provided by `kobweb-silk` (no new dependency). `ContactPrompt` owns the empty-message validation
state (FR-011) and, on a valid send, builds and navigates to the `mailto:` URI itself (fixed
recipient/subject constants passed in or hardcoded per FR-010).

**Rationale**: `TextInput`/`Button` render real `<input>`/`<button>` elements, which are natively
labelable, focusable, and keyboard-operable — satisfying the same accessibility default the seven
Phase 2 components already established (constitution Principle V) without hand-rolling ARIA. This
keeps the FR-014 exception as small as possible: one thin composable over existing Silk widgets,
not a new UI primitive.

**Alternatives considered**: Raw HTML `Input`/`Button` via Compose HTML directly (bypassing Silk)
— rejected, Silk's versions already carry the project's established styling/token integration
patterns every other component uses (`toSitePalette()`, `CssStyle`).

## 7. `Tag` usage on the project detail view

**Decision**: The project detail view renders each of the project's tags via the existing `Tag`
component (FR-014: reuse, don't reintroduce a plain-text chip), with every tag's `href` pointing
back at `/projects` (FR-009 from Phase 2 requires *a* navigation target; no tag-filtered view
exists yet, and building one is out of this phase's scope per the spec).

**Rationale**: Satisfies Phase 2's "Tag is always clickable" contract with the simplest defensible
target given no filtering feature exists — avoids inventing a `/projects?tag=X` URL scheme this
phase has no reader for.

**Alternatives considered**: Non-clickable tag display on the detail page only — rejected, `Tag`
has no non-interactive mode (Phase 2 FR-009: "never a purely static, non-interactive label").

## 8. Bilingual content storage for in-source lists and `trophies.json` (analyze findings C1/C2)

**Decision**: Every in-source list this phase adds (`projects`, About's timeline, CV's sections)
stores translatable fields as `BilingualString`, resolved to the plain-`String` fields Phase 2's
components actually take via a small `toXxx(lang)`/`resolve(lang)` mapper called at render time,
keyed off `LocalLang.current` (data-model.md). For `trophies.json` specifically: game titles and
trophy names are treated as proper-noun content pulled from an external, PSN-sourced automation
(Phase 4) and are rendered as-is in both languages; only the stat *labels* ("Total Trophies" etc.)
are bilingual, supplied by a small page-owned `Map<String, BilingualString>` keyed to each fetched
stat's stable `key` (data-model.md `TrophiesStat`).

**Rationale**: Phase 2's components were deliberately built to take only already-resolved
`String`s (Phase 2 FR-013) — the caller decides *when* to resolve a language, which every prior
phase's pages did trivially for single labels (`HomeLabel(LocalLang.current)`) but this phase is
the first to do it across whole *lists* of content, which wasn't worked out during
`/speckit-plan`. Treating fetched game/trophy names as untranslated proper nouns mirrors
constitution Principle III's own "technical labels...stay English" carve-out, extended to
externally-sourced real-world names a personal automation has no sensible translation for — while
still holding the page's *own* UI copy (stat labels) to the full bilingual bar.

**Alternatives considered**: Storing pre-resolved `String`s per language as two parallel lists
(`projectsEn`, `projectsPl`) — rejected, duplicates every list and drifts easily (exactly what
`BilingualString` already exists to prevent, per `Lang.kt`'s established pattern). Fully
translating `trophies.json` content (dual-language keys per game/trophy) — rejected as
disproportionate for real-world proper nouns with no natural translation, and would require
Phase 4's PSN automation to generate translations it has no source for.
