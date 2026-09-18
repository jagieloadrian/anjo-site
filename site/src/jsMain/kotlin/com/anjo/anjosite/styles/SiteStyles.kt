package com.anjo.anjosite.styles

// Single mount point for AppEntry.kt: combines every single-responsibility stylesheet above into
// one list of CSS rules. Add a new file's `.cssRules` here when adding a new stylesheet.
object SiteStyles {
    // specs/007-tests-polishing T012/T024: SiteOverlayStyles' `@media (prefers-reduced-motion:
    // reduce)` block overrides `.glitch:hover`/`.caret`'s unconditional `animation` rules in
    // SiteGlitchStyles — same selector specificity, so whichever comes LAST in this concatenation
    // wins the cascade regardless of the media query. Moved to the end (was first) so the
    // override actually overrides; verified via Playwright with `reducedMotion: "reduce"`.
    val cssRules = SiteTokenStyles.cssRules +
        SiteGlitchStyles.cssRules +
        SiteNavStyles.cssRules +
        SiteLayoutStyles.cssRules +
        SiteTypographyStyles.cssRules +
        SiteButtonStyles.cssRules +
        SiteTagStyles.cssRules +
        SiteTerminalStyles.cssRules +
        SiteStatsStyles.cssRules +
        SiteCardStyles.cssRules +
        SiteTimelineListStyles.cssRules +
        SiteMiscStyles.cssRules +
        SiteFooterStyles.cssRules +
        SiteResponsiveStyles.cssRules +
        SiteOverrideStyles.cssRules +
        SiteOverlayStyles.cssRules
}
