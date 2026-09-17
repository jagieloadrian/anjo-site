package com.anjo.anjosite

import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.CSSPosition
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.PointerEvents
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.Background
import com.varabyte.kobweb.compose.css.BackgroundImage
import com.varabyte.kobweb.compose.css.BackgroundSize
import com.varabyte.kobweb.compose.css.overflowX
import com.varabyte.kobweb.compose.css.functions.LinearGradient
import com.varabyte.kobweb.compose.css.functions.RadialGradient
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.css.functions.radialGradient
import com.varabyte.kobweb.compose.css.functions.repeatingLinearGradient
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.components.layout.HorizontalDividerStyle
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.CssRule
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.addVariantBase
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.silk.theme.modifyStyleBase
import org.jetbrains.compose.web.css.*

@InitSilk
fun initSiteStyles(ctx: InitSilkContext) {
    // This site does not need scrolling itself, but this is a good demonstration for how you might enable this in your
    // own site. Note that we only enable smooth scrolling unless the user has requested reduced motion, which is
    // considered a best practice.
    ctx.stylesheet.registerStyle("html") {
        cssRule(CSSMediaQuery.MediaFeature("prefers-reduced-motion", StylePropertyValue("no-preference"))) {
            Modifier.scrollBehavior(ScrollBehavior.Smooth)
        }
    }

    // Global reset — ports docs/handoff/styles.css lines 18-36 (FR-004).
    ctx.stylesheet.registerStyleBase("*") {
        Modifier.boxSizing(BoxSizing.BorderBox)
    }

    ctx.stylesheet.registerStyleBase("html, body") {
        Modifier
            .margin(0.px)
            .padding(0.px)
            .maxWidth(100.percent)
            .styleModifier { overflowX(Overflow.Hidden) }
            .fontFamily("Archivo", "system-ui", "sans-serif")
            .fontSize(18.px)
            .lineHeight(1.5)
    }

    ctx.stylesheet.registerStyleBase("a") {
        Modifier
            .color(SitePalettes.dark.pink)
            .textDecorationLine(TextDecorationLine.None)
    }
    ctx.stylesheet.registerStyleBase("a:hover") {
        Modifier.color(SitePalettes.dark.red)
    }
    ctx.stylesheet.registerStyleBase("::selection") {
        Modifier
            .backgroundColor(SitePalettes.dark.pink)
            .color(SitePalettes.dark.background)
    }
    ctx.stylesheet.registerStyleBase("button:focus-visible, a:focus-visible, input:focus-visible") {
        Modifier
            .outline(width = 2.px, style = LineStyle.Solid, color = SitePalettes.dark.pink)
            .outlineOffset(2.px)
    }

    // Print (specs/004-pages, real-browser print-preview check): Surface/SmoothColorStyle paints
    // the dark theme background+text unconditionally via a CSS class, so a plain `html, body`
    // print rule can't out-specificity it (`!important` is unsupported by Kobweb's CssStyle DSL —
    // throws IllegalStateException at runtime). An id selector always outranks a class selector,
    // regardless of stylesheet registration order, so `#site-surface` (see AppEntry.kt) wins.
    ctx.stylesheet.registerStyle("#site-surface") {
        base { Modifier }
        cssRule(CSSMediaQuery.MediaType(CSSMediaQuery.MediaType.Enum.Print)) {
            Modifier.backgroundColor(Colors.White).color(Colors.Black)
        }
    }

    // Decorative overlays — ports docs/handoff/styles.css lines 38-56 (FR-005).
    // Suppressed under reduced-motion: only the scanline; vignette/grid are static (FR-006, research.md §3).
    ctx.stylesheet.registerStyle(".fx-scan") {
        base {
            Modifier
                .position(Position.Fixed)
                .top(0.px).left(0.px).right(0.px).bottom(0.px)
                .zIndex(60)
                .opacity(0.55f)
                .backgroundImage(
                    repeatingLinearGradient(LinearGradient.Direction.ToBottom) {
                        add(Color.rgba(0, 0, 0, 0.38f), 0.px)
                        add(Color.rgba(0, 0, 0, 0.38f), 1.px)
                        add(Colors.Transparent, 1.px)
                        add(Colors.Transparent, 3.px)
                    }
                )
                .pointerEvents(PointerEvents.None)
        }
        cssRule(CSSMediaQuery.MediaFeature("prefers-reduced-motion", StylePropertyValue("reduce"))) {
            Modifier.display(DisplayStyle.None)
        }
        // Print (specs/004-pages, real-browser print-preview check): none of these neon overlays
        // belong on a printed page — same rationale as hiding nav/footer (FR-017).
        cssRule(CSSMediaQuery.MediaType(CSSMediaQuery.MediaType.Enum.Print)) {
            Modifier.display(DisplayStyle.None)
        }
    }

    ctx.stylesheet.registerStyle(".fx-vignette") {
        base {
            Modifier
                .position(Position.Fixed)
                .top(0.px).left(0.px).right(0.px).bottom(0.px)
                .zIndex(61)
                .backgroundImage(
                    radialGradient(
                        RadialGradient.Shape.Ellipse(120.percent, 80.percent),
                        CSSPosition(50.percent, 0.percent)
                    ) {
                        add(Colors.Transparent, 40.percent)
                        add(Color.rgba(0, 0, 0, 0.55f), 100.percent)
                    }
                )
                .pointerEvents(PointerEvents.None)
        }
        cssRule(CSSMediaQuery.MediaType(CSSMediaQuery.MediaType.Enum.Print)) {
            Modifier.display(DisplayStyle.None)
        }
    }

    ctx.stylesheet.registerStyle(".fx-grid") {
        base {
            Modifier
                .position(Position.Absolute)
                .top(0.px).left(0.px).right(0.px).bottom(0.px)
                .zIndex(0)
                .background(
                    Background.of(
                        image = BackgroundImage.of(
                            linearGradient { add(Color.rgba(255, 45, 149, 0.07f), 1.px); add(Colors.Transparent, 1.px) }
                        ),
                        size = BackgroundSize.of(96.px, 96.px)
                    ),
                    Background.of(
                        image = BackgroundImage.of(
                            linearGradient(90.deg) {
                                add(Color.rgba(255, 45, 149, 0.07f), 1.px); add(Colors.Transparent, 1.px)
                            }
                        ),
                        size = BackgroundSize.of(96.px, 96.px)
                    ),
                )
                .pointerEvents(PointerEvents.None)
        }
        cssRule(CSSMediaQuery.MediaType(CSSMediaQuery.MediaType.Enum.Print)) {
            Modifier.display(DisplayStyle.None)
        }
    }

    // Silk dividers only extend 90% by default; we want full width dividers in our site
    ctx.theme.modifyStyleBase(HorizontalDividerStyle) {
        Modifier.fillMaxWidth()
    }
}

val HeadlineTextStyle = CssStyle.base {
    Modifier
        .fontSize(3.cssRem)
        .textAlign(TextAlign.Start)
        .lineHeight(1.2) //1.5x doesn't look as good on very large text
}

val SubheadlineTextStyle = CssStyle.base {
    Modifier
        .fontSize(1.cssRem)
        .textAlign(TextAlign.Start)
        .color(colorMode.toPalette().color.toRgb().copyf(alpha = 0.8f))
}

val CircleButtonVariant = ButtonStyle.addVariantBase {
    Modifier.padding(0.px).borderRadius(50.percent)
}

val UncoloredButtonVariant = ButtonStyle.addVariantBase {
    Modifier.setVariable(ButtonVars.BackgroundDefaultColor, Colors.Transparent)
}

// Shared 48px touch target under the mock's 720px breakpoint (constitution Principle V), for
// plain Link/Button/TextInput usages that aren't one of the seven Phase 2 components (which each
// already bake this into their own CssStyle). `display(InlineFlex)` is required because a bare
// `<a>`/`<input>` is `inline` by default and ignores `min-height` otherwise (specs/004-pages,
// caught by a real-browser mobile-check pass).
val TouchTargetStyle = CssStyle {
    base { Modifier }
    (CssRule.OfMedia(CSSMediaQuery.MediaFeature("max-width", 720.px))) {
        Modifier.display(DisplayStyle.LegacyInlineFlex).alignItems(AlignItems.Center).minHeight(48.px)
    }
}
