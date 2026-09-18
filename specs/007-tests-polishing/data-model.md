# Phase 1 Data Model: Polish + Automated Test Suite

No database, no new domain entity. This phase's only new "shapes" are (a) the extended per-page
metadata `PageLayoutData` carries, (b) the `<head>` output contract it produces (what social/
search crawlers actually read), and (c) the functions newly exposed or extracted for unit
testing. See
`research.md` for the rationale behind each.

## `PageLayoutData` (extended)

`site/src/jsMain/kotlin/com/anjo/anjosite/components/layouts/PageLayout.kt`

```kotlin
class PageLayoutData(
    val title: String,
    val description: BilingualString,
    val ogImage: String = "/og-banner.png",
)
```

- `title`: unchanged (already existed) — feeds `document.title`.
- `description`: NEW, required — every page's `@InitRoute` call site (FR-001) supplies its own
  `BilingualString`, resolved via `LocalLang.current` the same way every other page copy is.
- `ogImage`: NEW, defaults to the site-wide fallback banner (research.md §8). A project detail
  page's *real* `og:image` can't be known at static `@InitRoute` time (it depends on a runtime
  fetch) — `Slug.kt` instead calls the shared `updatePageMeta(...)` helper directly, a second time,
  once its fetch resolves (see below).

## `updatePageMeta` (new, shared helper in `PageLayout.kt`)

```kotlin
fun updatePageMeta(title: String, description: String, ogImage: String)
```

Sets `document.title` and creates/updates the five `<head>` elements below. Called from two
places: `PageLayout.kt`'s existing `LaunchedEffect` (using `PageLayoutData`'s static values, for
every page) and `Slug.kt`'s `SlugPage()` (a second call, in its own `LaunchedEffect(project)`,
once the fetched project is known — the one route whose real title/description/`og:image` can't
be known until runtime).

## `<head>` output contract (per page, written by `updatePageMeta`)

```
<title>Adrian Jagieło — {title}</title>
<meta name="description" content="{description(lang)}">
<meta property="og:title" content="Adrian Jagieło — {title}">
<meta property="og:description" content="{description(lang)}">
<meta property="og:type" content="website">
<meta property="og:image" content="{ogImage}">
```

- Confirmed (research.md §4) that Kobweb's static export snapshots the DOM *after* this
  `LaunchedEffect` runs, so every field above lands in the real exported `.html` file, not just
  the live dev-server DOM.
- `og:type` is a fixed literal (`"website"`) — the spec doesn't distinguish page types (e.g.
  `article` for a project) and nothing downstream reads it as anything but a constant.

## Newly test-exposed functions (no shape change, visibility/extraction only)

| Function | File | Change | Covered by |
|---|---|---|---|
| `langForLocale(locale: String): Lang` | `Lang.kt` | NEW — extracted, pure, from `detectInitialLang()`'s body (Kotlin/JS browser-target tests run in a real Karma browser, so `window.navigator.language` isn't controllable from a test — research.md §1) | `LangTest.kt` (FR-008) |
| `detectInitialLang(): Lang` | `Lang.kt` | unchanged (now a thin wrapper: `langForLocale(window.navigator.language)`) | not directly tested — see `langForLocale` |
| `BilingualString.invoke(lang): String` | `Lang.kt` | none (already public) | `LangTest.kt` (FR-008) |
| `BilingualTimelineItem.resolve(lang): TimelineItem` | `Lang.kt` | none (already public) | `LangTest.kt` (FR-008) |
| `parseTrophiesData(text: String): TrophiesData` | `Trophies.kt` | `private` → `internal` | `TrophiesTest.kt` (FR-009) |
| `parseProjectEntries(text: String): List<ProjectEntry>` | `Projects.kt` | none (already public) | `ProjectsTest.kt` (FR-009) |
| `findProjectBySlug(entries, slug): ProjectEntry?` | `Slug.kt` | NEW — extracted from `SlugPage()`'s inline lookup | `SlugTest.kt` (FR-010) |

No other production type changes.
