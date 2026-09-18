package com.anjo.anjosite.styles

// Single mount point for AppEntry.kt: combines every single-responsibility stylesheet above into
// one list of CSS rules. Add a new file's `.cssRules` here when adding a new stylesheet.
object SiteStyles {
    val cssRules = SiteTokenStyles.cssRules +
        SiteOverlayStyles.cssRules +
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
        SiteOverrideStyles.cssRules
}
