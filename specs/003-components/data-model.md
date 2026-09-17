# Phase 1 Data Model: Shared UI Components

Each entity is a small, immutable `data class` or `enum`, co-located in the same file as the
composable that renders it (per `plan.md`'s Structure Decision). None has persistence, identity,
or a relationship to another entity — see spec Assumptions ("components are presentational
only") and the Phase 2 research decision (§3) that each clickable component owns its own
navigation target rather than referencing another entity's.

## TagVariant (`Tag.kt`)

```kotlin
enum class TagVariant { TECH_STACK, STATUS }
```

- Selects `Tag`'s visual style (FR-009). An unrecognized/absent variant is not representable —
  Kotlin's exhaustive `enum` plus a `when` with no `else` branch makes "unrecognized variant" a
  compile error instead of a runtime fallback path, which is a stronger guarantee than the spec's
  minimum ("falls back to a default style") without any extra code.

**`Tag` composable contract**:

```kotlin
@Composable
fun Tag(text: String, variant: TagVariant, href: String)
```

- `text`, `variant`, `href` all non-nullable/required — FR-009 (always clickable, never a static
  label) is enforced at the call site by the compiler.

## StatItem (`StatRow.kt`)

```kotlin
data class StatItem(val label: String, val value: String)
```

- `value` may be an empty string (FR-006's "renders without error when the value is empty") —
  deliberately `String`, not `String?`, so an absent value is the caller's explicit choice
  (`""`), not a null the component has to branch on.

**`StatRow` composable contract**:

```kotlin
@Composable
fun StatRow(item: StatItem)
```

## TimelineItem (`TimelineEntry.kt`)

```kotlin
data class TimelineItem(val date: String, val title: String, val description: String)
```

- `date` is a display-ready `String` (already localized/formatted by the caller), not a `LocalDate`
  — this component has no notion of date parsing/formatting/sorting (out of scope; Phase 3/4's
  concern if ever needed).

**`TimelineEntry` composable contract**:

```kotlin
@Composable
fun TimelineEntry(item: TimelineItem)
```

## ProjectSummary (`ProjectCard.kt`)

```kotlin
data class ProjectSummary(
    val title: String,
    val description: String,
    val tags: List<String>,
    val coverImageUrl: String?,
    val coverImageAlt: String,
    val href: String,
)
```

- `coverImageUrl` nullable (FR-004/edge case: missing cover → placeholder, research.md §4).
  `coverImageAlt` is required and non-null regardless of whether an image ends up rendering — it
  doubles as the card `Link`'s accessible-name contribution when a cover is present (research.md
  §3), and costs nothing to require even for the placeholder case.
- `href` required (FR-004: card is itself the clickable container).

**`ProjectCard` composable contract**:

```kotlin
@Composable
fun ProjectCard(project: ProjectSummary)
```

## GameCoverImage (`GameCover.kt`)

```kotlin
data class GameCoverImage(val imageUrl: String?, val alt: String, val href: String)
```

- Same nullable-URL/placeholder shape as `ProjectSummary.coverImageUrl` (FR-007). Independent of
  `TrophyEntry` (research/clarification: `GameCover` and `TrophyRow` are separate, standalone
  components — no shared/parent entity).

**`GameCover` composable contract**:

```kotlin
@Composable
fun GameCover(cover: GameCoverImage)
```

## TrophyEntry (`TrophyRow.kt`)

```kotlin
data class TrophyEntry(
    val tier: TrophyTier,
    val name: String,
    val earnedAt: String?,
    val href: String,
)

enum class TrophyTier { BRONZE, SILVER, GOLD, PLATINUM }
```

- `earnedAt` nullable = locked/unearned state (FR-008 edge case); a display-ready `String` for the
  same reason as `TimelineItem.date` above.
- `tier` drives the icon shown (FR-008's "tier/icon") — modeled as an `enum` rather than a raw
  icon reference so `TrophyRow` owns the tier→icon mapping internally, consistent with "content as
  data" (the caller supplies *what tier*, not *which icon asset*).

**`TrophyRow` composable contract**:

```kotlin
@Composable
fun TrophyRow(trophy: TrophyEntry)
```

## TerminalLine (`Terminal.kt`)

```kotlin
data class TerminalLine(val text: String, val style: TerminalLineStyle)

enum class TerminalLineStyle { COMMAND, OUTPUT, ACCENT_PINK, ACCENT_CYAN }
```

- Mirrors the mock's `[text, cssClass]` tuples (`docs/handoff/app.js`'s `BOOT.en`/`BOOT.pl`
  arrays) one-to-one — `COMMAND` = `t-cmd` (the 26ms/char timing class per research.md §5),
  everything else = the 16ms/char timing. The caller supplies one already-localized
  `List<TerminalLine>` per render (per FR-013 — `Terminal` has no language switch of its own); a
  page that needs bilingual boot text picks the right list before passing it in.

**`Terminal` composable contract**:

```kotlin
@Composable
fun Terminal(lines: List<TerminalLine>)
```
