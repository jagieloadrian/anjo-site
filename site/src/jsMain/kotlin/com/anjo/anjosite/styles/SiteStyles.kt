package com.anjo.anjosite.styles

object SiteStyles {
    // Order matters: equal-specificity rules resolve by source position, so SiteOverlayStyles must stay last.
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
