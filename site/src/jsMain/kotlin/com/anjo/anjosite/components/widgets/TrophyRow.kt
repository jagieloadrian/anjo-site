package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.clamp
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssRule
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.*
import com.anjo.anjosite.SitePalette
import com.anjo.anjosite.toSitePalette

enum class TrophyTier { BRONZE, SILVER, GOLD, PLATINUM }

data class TrophyEntry(
    val tier: TrophyTier,
    val name: String,
    val earnedAt: String?,
    val href: String,
)

val TrophyRowStyle = CssStyle {
    base {
        Modifier.fillMaxWidth().gap(0.75.cssRem).padding(topBottom = 0.75.cssRem)
    }
    (CssRule.OfMedia(CSSMediaQuery.MediaFeature("max-width", 720.px))) {
        Modifier.minHeight(48.px)
    }
}

val TrophyNameStyle = CssStyle.base {
    Modifier
        .fontFamily("Archivo", "system-ui", "sans-serif")
        .fontWeight(700)
        .fontSize(clamp(0.9375.cssRem, 1.8.vw, 1.0625.cssRem))
}

val TrophyMetaStyle = CssStyle.base {
    Modifier
        .fontFamily("JetBrains Mono", "monospace")
        .fontSize(clamp(0.6875.cssRem, 1.2.vw, 0.75.cssRem))
}

// Tier → color mapping owned internally by this component ("content as data": callers supply
// the tier, not an icon asset — data-model.md TrophyEntry).
private fun tierColor(tier: TrophyTier, sitePalette: SitePalette) = when (tier) {
    TrophyTier.BRONZE -> sitePalette.red
    TrophyTier.SILVER -> sitePalette.ink
    TrophyTier.GOLD -> sitePalette.pink
    TrophyTier.PLATINUM -> sitePalette.cyan
}

@Composable
fun TrophyRow(trophy: TrophyEntry) {
    val sitePalette = ColorMode.current.toSitePalette()
    val dotColor = tierColor(trophy.tier, sitePalette)
    Link(
        trophy.href,
        modifier = TrophyRowStyle.toModifier(),
        variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
    ) {
        Row(Modifier.fillMaxWidth().gap(0.75.cssRem), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .width(0.75.cssRem)
                    .height(0.75.cssRem)
                    .borderRadius(50.percent)
                    .backgroundColor(if (trophy.earnedAt != null) dotColor else sitePalette.nearBackground)
            )
            Column(Modifier.gap(0.125.cssRem)) {
                SpanText(trophy.name, TrophyNameStyle.toModifier())
                SpanText(
                    trophy.earnedAt ?: "locked",
                    TrophyMetaStyle.toModifier().color(sitePalette.ink.toRgb().copyf(alpha = 0.6f))
                )
            }
        }
    }
}
