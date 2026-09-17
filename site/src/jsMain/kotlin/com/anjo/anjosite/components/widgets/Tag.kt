package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.clamp
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssRule
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.*
import com.anjo.anjosite.toSitePalette

enum class TagVariant { TECH_STACK, STATUS }

// Pill shape only — variant color is applied at the call site (research.md §3: Tag is always a
// Link, never a plain span).
val TagStyle = CssStyle {
    base {
        Modifier
            .padding(leftRight = 0.75.cssRem, topBottom = 0.375.cssRem)
            .borderRadius(999.px)
            .fontFamily("JetBrains Mono", "monospace")
            .fontSize(clamp(0.6875.cssRem, 1.5.vw, 0.75.cssRem))
    }
    // 48px touch target under the mock's 720px breakpoint (FR-010, research.md §2) — a raw
    // media query since Silk's Breakpoint scale has no step at exactly 720px.
    (CssRule.OfMedia(CSSMediaQuery.MediaFeature("max-width", 720.px))) {
        Modifier.minHeight(48.px)
    }
}

@Composable
fun Tag(text: String, variant: TagVariant, href: String) {
    val sitePalette = ColorMode.current.toSitePalette()
    val accent = when (variant) {
        TagVariant.TECH_STACK -> sitePalette.cyan
        TagVariant.STATUS -> sitePalette.pink
    }
    Link(
        href,
        modifier = TagStyle.toModifier()
            .border(1.px, LineStyle.Solid, accent)
            .color(accent)
            .backgroundColor(accent.toRgb().copyf(alpha = 0.1f)),
        variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
    ) {
        SpanText(text)
    }
}
