# Phase 1 Data Model: Layout and Routing

This phase introduces one small piece of in-memory client state (language) and no persisted or
server-side data (constitution Principle I). The "entities" below are Kotlin types, included
because the spec's Key Entities section names them.

## Route

Represents a single navigable destination, backed by one `@Page`-annotated file under `pages/`.

| Field | Type | Source | Notes |
|---|---|---|---|
| `path` | `String` | filename (or `routeOverride`) | e.g. `/`, `/about`, `/projects`, `/trophies`, `/contact`, `/cv`, `/404` |
| `exportPath` | `String` | Kobweb export logic | `route + ".html"`, or `index.html` if route ends in `/` (research.md §1–2) |

Not a Kotlin class in this phase — represented implicitly by each `@Page` file's existence; no
route registry or manifest is hand-maintained (Kobweb's KSP processor derives it at compile
time).

Relationship: `NavHeader.kt`'s six `Link(...)` calls each reference one Route's `path` by string
literal — no dynamic route list is needed for six fixed, known destinations.

## Lang (Language State)

The single shared value determining which stored string value each Bilingual String renders.

| Field | Type | Notes |
|---|---|---|
| enum values | `Lang.EN`, `Lang.PL` | Exhaustive — no third language planned |

| Concept | Type | Notes |
|---|---|---|
| `LocalLang` | `ProvidableCompositionLocal<Lang>` | Provided once, at `AppEntry.kt`'s app-shell level (research.md §3) |
| `LocalLangSetter` | `ProvidableCompositionLocal<(Lang) -> Unit>` | Paired setter, provided alongside `LocalLang` at the same call site (research.md §3 addendum) — only `NavHeader.kt`'s language switch reads it |
| initial value | `Lang` | `Lang.PL` if `window.navigator.language` starts with `"pl"`, else `Lang.EN` (research.md §4) |

Validation rule: exactly one `CompositionLocal` provider exists for `Lang`/its setter in the whole
app (in `AppEntry.kt`) — no composable creates a second, competing source of truth.

Lifecycle: initialized once per page load from the browser signal; changed only by the nav's
language switch (in-memory `mutableStateOf`, no persistence — spec Edge Cases explicitly defer
cross-page persistence).

## Bilingual String

A piece of UI text with exactly one English value and one Polish value.

| Field | Type | Notes |
|---|---|---|
| `en` | `String` | English value |
| `pl` | `String` | Polish value |

Represented as a small data holder (e.g. a two-field class or a `Lang -> String` selection
function) consumed alongside `LocalLang.current` — not a duplicated pair of composables, per
FR-010. This phase uses this shape in two places: the Index placeholder's demo string (proving
the mechanism itself), and the 404 page's not-found message (real user-facing prose, required to
be bilingual by constitution Principle III — see spec.md Assumptions). It is not a generalized
content-loading system (that's Phase 3's "content as data" work, constitution Principle II).

Relationship: every Bilingual String reads `LocalLang.current` to select its rendered value —
never branches on a locally-duplicated language check per call site.
