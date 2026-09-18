# Phase 1 Data Model: Pages

Every entity below is either reused as-is from Phase 2 (specs/003-components) or a small,
immutable `data class` new to this phase, co-located with the page/component that owns it. None
has persistence beyond the process — the projects/about/CV lists are in-source `List` literals
(FR-006/FR-012); `TrophiesData` is the one entity that arrives over the network (FR-007).

## Bilingual content pattern (analyze finding C1)

Phase 2's reused entities (`ProjectSummary`, `TimelineItem`, `StatItem`) all take plain,
already-resolved `String` fields by design (Phase 2 FR-013: components have no translation logic
of their own). That means every in-source list this phase adds MUST store translatable text as
`BilingualString` and resolve it to a plain-`String` Phase 2 entity **at render time**, keyed off
`LocalLang.current` — the same shape `Terminal`'s per-language `List<TerminalLine>` selection
already established in Phase 2. Concretely: `ProjectEntry.title`/`shortDescription`/
`fullDescription`/`coverImageAlt` and `BilingualTimelineItem`'s three fields (below) are
`BilingualString`, not `String`; each page calls a small `toXxx(lang)` mapper before constructing
the Phase 2 component's data class. `tags: List<String>` stays plain — tech/stack tags are exactly
the "technical labels...stay English in both languages" carve-out constitution Principle III
already names.

`BilingualTimelineItem` and its mapper are added to `Lang.kt` (not a new file) — it's used by both
`About.kt` and `Cv.kt`, and `Lang.kt` already owns every other cross-cutting bilingual helper
(`HomeLabel`, `BilingualString` itself, etc.):

```kotlin
// Lang.kt addition
data class BilingualTimelineItem(val date: BilingualString, val title: BilingualString, val description: BilingualString)
fun BilingualTimelineItem.resolve(lang: Lang) = TimelineItem(date(lang), title(lang), description(lang))
```

## ProjectEntry (`pages/Projects.kt`, new)

```kotlin
data class ProjectEntry(
    val slug: String,
    val title: BilingualString,
    val shortDescription: BilingualString,   // the card's short description
    val fullDescription: BilingualString,    // longer prose for the detail view (FR-005 acceptance scenario 3)
    val tags: List<String>,                  // tech/stack tags — English in both languages (constitution III)
    val coverImageUrl: String?,
    val coverImageAlt: BilingualString,
    val repoUrl: String? = null,             // "any linked assets" — e.g. a GitHub/GitLab link; absent if none
)

fun ProjectEntry.toSummary(lang: Lang) = ProjectSummary(
    title = title(lang),
    description = shortDescription(lang),
    tags = tags,
    coverImageUrl = coverImageUrl,
    coverImageAlt = coverImageAlt(lang),
    href = "/projects/$slug",
)
```

- `slug` is this phase's identity/uniqueness rule (clarification session, Q1): unique per
  project, used both by the dynamic route (`/projects/{slug}`) and by `build.gradle.kts`'s
  `extraRoutes` static-export registration (research.md §1).
- `toSummary(lang)` is called at render time (grid and detail page alike) to produce the plain-
  `String` `ProjectSummary` Phase 2's `ProjectCard` actually takes — `href` is derived from `slug`
  here rather than stored twice.

**In-source data**: `val projects: List<ProjectEntry>` — a plain Kotlin list literal in
`Projects.kt` (FR-006). Empty list is valid (edge case: grid renders empty state, US1 scenario 4).

## AboutContent (`pages/About.kt`, new)

```kotlin
data class AboutContent(val bio: BilingualString, val timeline: List<BilingualTimelineItem>)
```

- `timeline` is `List<BilingualTimelineItem>` (see above), resolved via `.resolve(lang)` per item
  at render time — not Phase 2's `TimelineItem` directly (analyze finding C1).
- `bio` is a `BilingualString`, same pattern as `HomeLabel`/`AboutLabel` etc. in `Lang.kt`.

## CvContent (`pages/Cv.kt`, new)

```kotlin
data class CvSection(val heading: BilingualString, val entries: List<BilingualTimelineItem>)
data class CvContent(val sections: List<CvSection>)
```

- Intentionally its own shape (FR-012) — not a reuse of `AboutContent` or `ProjectEntry` even
  though the underlying facts (career history) overlap with About's timeline; CV groups entries
  under section headings (e.g. "Experience", "Skills") that About doesn't need.
- Reuses `BilingualTimelineItem` per section rather than inventing a fourth "entry" shape.

## TrophiesData (`pages/Trophies.kt`, new — the shape of `trophies.json`)

Analyze finding C2 resolution: `trophies.json` is fetched once from an external, PSN-sourced
automation (Phase 4, ROADMAP F025) — game titles and trophy names are treated as **proper-noun
content data**, not editorial UI copy, and are rendered as-is in both languages (a documented
exception to FR-013, parallel to constitution III's technical-label carve-out: a personal
automation pulling real PlayStation data has no sensible Polish translation for "Elden Ring" or a
specific trophy's name). Stat *labels* ("Total Trophies", "Completion Rate", etc.), by contrast,
are UI copy this phase owns and MUST be bilingual — so each stat carries a stable `key` the page
maps to a `BilingualString` label, while its `value` comes from the fetched JSON:

```kotlin
data class TrophiesStat(val key: String, val value: String)   // key e.g. "total", "platinum", "completion"
data class TrophiesData(
    val stats: List<TrophiesStat>,
    val games: List<GameCoverImage>,    // reused from Phase 2 (GameCover.kt) — name/alt not translated (see above)
    val trophies: List<TrophyEntry>,    // reused from Phase 2 (TrophyRow.kt) — name not translated (see above)
)

sealed interface TrophiesFetchState {
    data object Loading : TrophiesFetchState
    data class Loaded(val data: TrophiesData) : TrophiesFetchState
    data object Failed : TrophiesFetchState
}

// Trophies.kt — page-owned bilingual labels, keyed to match trophies.json's stat "key" values
val trophiesStatLabels: Map<String, BilingualString> = mapOf(
    "total" to BilingualString(en = "Total Trophies", pl = "Wszystkie trofea"),
    "platinum" to BilingualString(en = "Platinum", pl = "Platyna"),
    "completion" to BilingualString(en = "Completion", pl = "Ukończenie"),
)
fun TrophiesStat.toStatItem(lang: Lang) = StatItem(
    label = trophiesStatLabels.getValue(key)(lang),
    value = value,
)
```

- `TrophiesFetchState` is the loading/error/success state driving FR-007/FR-009/FR-016 — held in
  `remember { mutableStateOf<TrophiesFetchState>(Loading) }` and set once the `fetch()` promise
  resolves or rejects (research.md §3).
- Parsed manually from `JSON.parse<dynamic>(text)` (research.md §4) — no `@Serializable`
  annotations, since `kotlinx.serialization` isn't a project dependency.

## ContactMessage / ContactPrompt (`components/widgets/ContactPrompt.kt`, new — FR-014 exception)

```kotlin
data class ContactMessage(val body: String)
```

- Single free-text field per clarification session (mock parity) — recipient and subject are
  fixed constants passed into/hardcoded within `ContactPrompt`, never part of this data class.

**`ContactPrompt` composable contract**:

```kotlin
@Composable
fun ContactPrompt(recipientEmail: String, subject: String)
```

- Owns its own `remember { mutableStateOf(ContactMessage("")) }` and empty-message validation
  state internally (FR-011) — the page (`Contact.kt`) supplies only the fixed recipient/subject,
  consistent with "content as data" (constitution Principle II) applied to the one new component
  this phase introduces.
- Builds the `mailto:` URI (`mailto:$recipientEmail?subject=...&body=...`, both parts
  `encodeURIComponent`-escaped) and navigates to it via `window.location.href =` on a valid send,
  entirely client-side (FR-010).

**Bilingual chrome text (analyze finding B1)**: `ContactPrompt`'s own visible copy — the send
button's label and the "message required" validation message — is real user-facing prose (FR-013)
with no natural place in the `recipientEmail`/`subject` parameters. It's read internally via
`LocalLang.current`, the same way `NavHeader.kt`'s `LangSwitchButton` already reads its own chrome
text without taking it as a parameter:

```kotlin
// ContactPrompt.kt — internal, not parameters (only recipient/subject vary by call site)
private val SendLabel = BilingualString(en = "Send", pl = "Wyślij")
private val MessageRequiredLabel = BilingualString(en = "Message required", pl = "Wiadomość jest wymagana")
```
