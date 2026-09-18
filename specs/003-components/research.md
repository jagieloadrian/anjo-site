# Phase 0 Research: Shared UI Components

## 1. Reduced-motion live reactivity for `Terminal`

**Decision**: Read `prefers-reduced-motion` once via `window.matchMedia("(prefers-reduced-motion: reduce)")`,
hold its `.matches` value in a `mutableStateOf<Boolean>`, and attach a `"change"` listener via
`MediaQueryList.addEventListener("change") { ... }` that updates that state. `Terminal`'s
`LaunchedEffect(reducedMotion)` keys off that state: when it's `true` the effect renders the full
text immediately and returns; when it flips to `true` mid-animation, the coroutine is
cancelled/restarted by the `LaunchedEffect` key change, which is enough to satisfy FR-003 (stop a
now-unwanted animation) — no manual coroutine bookkeeping needed.

**Rationale**: Every existing reduced-motion check in this codebase (`AppStyles.kt`'s `.fx-scan`,
`NavHeader.kt`'s `BrandStyle` hover) is a static CSS media query, which is enough to *disable a
style* but can't stop a running Kotlin coroutine — `Terminal` is the first component that needs
the JS-side signal. `matchMedia` + `addEventListener("change")` is the standard DOM API for this
and needs no new dependency (`kotlinx.browser` is already transitively available via
`kotlinx-browser`, same package `Lang.kt` already imports `window` from).

**Alternatives considered**: Polling `matchMedia(...).matches` on a timer — rejected, wasteful and
laggy compared to the native change event. CSS-only (`animation-play-state` toggled by a
`prefers-reduced-motion` rule) — rejected, `Terminal`'s reveal is driven by Kotlin state updates
(each keystroke is a recomposition), not a CSS `@keyframes` animation, so there's no CSS animation
to pause.

## 2. 720px touch-target breakpoint

**Decision**: Use a raw `CssRule.OfMedia(CSSMediaQuery.MediaFeature("max-width", 720.px))` scoped
rule (same `CssRule.OfMedia` mechanism `NavHeader.kt`'s `BrandStyle` already uses for
`prefers-reduced-motion`), not Silk's `Breakpoint` enum.

**Rationale**: FR-010 and the mock (`docs/handoff/styles.css:358`) both specify exactly 720px.
Kobweb's default `Breakpoint` scale (`SM`=640px, `MD`=768px, `LG`=1024px, `XL`=1280px) has no step
at 720px — the nearest values straddle it in both directions, so reusing `Breakpoint.SM` or
`.MD` would silently miss the spec's actual threshold. A raw media query reproduces the mock's
number exactly and costs nothing extra (the `CssRule.OfMedia` pattern is already established in
this codebase).

**Alternatives considered**: Redefine a custom `Breakpoint` scale — rejected, out of scope for
this phase and would ripple into `NavHeader.kt`'s existing `Breakpoint.MD` usage; not requested by
the spec. Rounding to the nearest existing `Breakpoint` — rejected, contradicts FR-010's explicit
number and the mock it was ported from.

## 3. Whole-component clickability (`ProjectCard`, `GameCover`, `TrophyRow`, `Tag`)

**Decision**: Each of these four composables wraps its entire visual content in Silk's `Link`
(the same composable `NavHeader.kt`'s `Brand()` already uses to make a multi-element `Row` a
single link), styled with `UndecoratedLinkVariant.then(UncoloredLinkVariant)` so the link doesn't
inherit default anchor styling, with the component's own `CssStyle` supplying the visual card/tag
look on top.

**Rationale**: `Link` renders a real `<a>`, which is natively focusable, keyboard-activatable, and
exposed to assistive tech with the correct implicit link role — satisfying FR-015 (ARIA
role/accessible label) without hand-rolling `role="link"` + `tabindex` + `onKeyDown` on a `Div`.
Per the browser's accessible-name computation, an `<a>` wrapping an `<img alt="...">` and/or text
already derives its accessible name from that content, so `GameCover` (image-only, no visible
text) gets a correct accessible name from its required `alt` (FR-014) with no extra `aria-label`
needed. This is the same pattern already proven in this codebase (`Brand()` wraps an icon `Div` +
`SpanText` in one `Link`).

**Alternatives considered**: `Modifier.onClick { ... }` on a plain `Div` with a manually added
`role="link"`/`tabindex="0"` — rejected, reinvents what `<a>` gives natively and is an easy source
of missed keyboard/AT support (exactly what FR-015 exists to prevent). A `<button>` — rejected,
semantically wrong for navigation (buttons are for actions, not for changing location), and the
existing codebase already reserves `Button`/`IconButton` for non-navigating actions
(`ColorModeButton`, `LangSwitchButton`).

## 4. Placeholder image fallback (`ProjectCard`, `GameCover`)

**Decision**: The image parameter is a nullable `String?` (image URL). When non-null, render
`org.jetbrains.compose.web.dom.Img(src = url, alt = altText)`. When `null`, render a fixed-size
`Div` styled with the component's own placeholder `CssStyle` (background token from
`SiteTheme.kt`, no `<img>` tag at all) — so there's never a broken-image icon, per the spec's edge
case.

**Rationale**: A conditional composable branch is the simplest possible mechanism (YAGNI) —
no image-loading library, no `onerror` JS interop needed, since "no URL supplied" and "URL fails
to load" are treated the same way by design (spec only requires handling the "no image supplied"
case; a network-level broken image is out of scope for this phase).

**Alternatives considered**: Always render an `<img>` with a placeholder URL as the `src` default
— rejected, adds a fake network request and a maintained placeholder asset for no benefit over a
CSS-only placeholder block.

## 5. `Terminal` typing-loop mechanism

**Decision**: A `LaunchedEffect` coroutine that walks the supplied `List<TerminalLine>` and, for
each line, appends one character at a time to a `mutableStateOf<String>` via
`kotlinx.coroutines.delay(...)`, using the mock's own timings ported as-is: 26ms/char when the
line's text starts with `"$"`, 16ms/char otherwise, 240ms pause between lines
(`docs/handoff/app.js`).

**Rationale**: `kotlinx.coroutines` is already a mandatory transitive dependency of
`androidx.compose.runtime` (which `LaunchedEffect`/`delay` come from) — no new Gradle dependency,
consistent with constitution Principle VIII. Porting the mock's exact timings preserves the
established "feel" the spec's Assumptions defer content/copy to the caller for, while keeping the
animation mechanics themselves faithful to the reference implementation.

**Alternatives considered**: `window.setTimeout` chains, mirroring the mock's own vanilla-JS
implementation literally — rejected, fighting Compose's coroutine-based effect model for no
benefit when `delay()` does the same job idiomatically within `LaunchedEffect`.
