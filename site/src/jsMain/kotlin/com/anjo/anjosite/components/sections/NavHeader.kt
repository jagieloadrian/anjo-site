package com.anjo.anjosite.components.sections

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.dom.ElementTarget
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.clamp
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.CloseIcon
import com.varabyte.kobweb.silk.components.icons.HamburgerIcon
import com.varabyte.kobweb.silk.components.icons.MoonIcon
import com.varabyte.kobweb.silk.components.icons.SunIcon
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.overlay.Overlay
import com.varabyte.kobweb.silk.components.overlay.OverlayVars
import com.varabyte.kobweb.silk.components.overlay.PopupPlacement
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssRule
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.animation.Keyframes
import com.varabyte.kobweb.silk.style.animation.toAnimation
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import com.anjo.anjosite.AboutLabel
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.ContactLabel
import com.anjo.anjosite.CvLabel
import com.anjo.anjosite.HomeLabel
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.LocalLangSetter
import com.anjo.anjosite.ProjectsLabel
import com.anjo.anjosite.TrophiesLabel
import com.anjo.anjosite.components.widgets.IconButton
import com.anjo.anjosite.toSitePalette

val NavHeaderStyle = CssStyle.base {
    Modifier.fillMaxWidth().padding(1.cssRem)
}

@Composable
private fun NavLink(path: String, label: BilingualString) {
    Link(path, label(LocalLang.current), variant = UndecoratedLinkVariant.then(UncoloredLinkVariant))
}

@Composable
private fun MenuItems() {
    NavLink("/", HomeLabel)
    NavLink("/about", AboutLabel)
    NavLink("/projects", ProjectsLabel)
    NavLink("/trophies", TrophiesLabel)
    NavLink("/contact", ContactLabel)
    NavLink("/cv", CvLabel)
}

@Composable
private fun ColorModeButton() {
    var colorMode by ColorMode.currentState
    IconButton(onClick = { colorMode = colorMode.opposite },) {
        if (colorMode.isLight) MoonIcon() else SunIcon()
    }
    Tooltip(ElementTarget.PreviousSibling, "Toggle color mode", placement = PopupPlacement.BottomRight)
}

@Composable
private fun Brand() {
    val sitePalette = ColorMode.current.toSitePalette()
    Link("/", variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)) {
        Row(Modifier.gap(0.75.cssRem), verticalAlignment = Alignment.CenterVertically) {
            Div(
                Modifier
                    .width(0.75.cssRem)
                    .height(0.75.cssRem)
                    .backgroundColor(sitePalette.red)
                    .toAttrs()
            )
            SpanText("ADRIAN JAGIEŁO", BrandStyle.toModifier())
        }
    }
}

// Language switch (spec 002-layout-routing FR-007/FR-009): toggles the app-wide LocalLang.
@Composable
private fun LangSwitchButton() {
    val lang = LocalLang.current
    val setLang = LocalLangSetter.current
    IconButton(onClick = { setLang(lang.other) }) {
        SpanText(lang.name, Modifier.fontFamily("JetBrains Mono", "monospace").fontSize(0.75.cssRem))
    }
    Tooltip(ElementTarget.PreviousSibling, "Switch language", placement = PopupPlacement.BottomRight)
}

@Composable
private fun HamburgerButton(onClick: () -> Unit) {
    IconButton(onClick) {
        HamburgerIcon()
    }
}

@Composable
private fun CloseButton(onClick: () -> Unit) {
    IconButton(onClick) {
        CloseIcon()
    }
}

// Brand hover glitch (spec 002-layout-routing FR-005, ported from docs/handoff/styles.css:65-82):
// a small position wobble. The mock's ::after cyan-ghost duplicate layer is skipped — the wobble
// alone reads as "glitch" and avoids a `content: attr(...)` hack Compose HTML has no direct
// Modifier for.
val BrandGlitchAnim = Keyframes {
    each(0.percent, 100.percent) { Modifier.translate(0.px, 0.px) }
    20.percent { Modifier.translate((-2).px, 1.px) }
    40.percent { Modifier.translate(2.px, (-1).px) }
    60.percent { Modifier.translate((-1).px, (-1).px) }
    80.percent { Modifier.translate(1.px, 1.px) }
}

val BrandStyle = CssStyle {
    base {
        Modifier
            .fontFamily("Archivo", "system-ui", "sans-serif")
            .fontWeight(900)
            .fontSize(1.cssRem)
            .letterSpacing(0.12.em)
            .textTransform(TextTransform.Uppercase)
    }
    hover {
        Modifier.animation(
            BrandGlitchAnim.toAnimation(
                duration = 220.ms,
                timingFunction = AnimationTimingFunction.steps(2),
                iterationCount = AnimationIterationCount.Infinite
            )
        )
    }
    // Reduced-motion override (constitution Principle V): cancel just the hover animation,
    // scoped the same way AppStyles.kt scopes .fx-scan's reduced-motion rule (research.md).
    (CssRule.OfMedia(CSSMediaQuery.MediaFeature("prefers-reduced-motion", StylePropertyValue("reduce"))) + hover) {
        Modifier.animation { name("none") }
    }
}

val SideMenuSlideInAnim = Keyframes {
    from {
        Modifier.translateX(100.percent)
    }

    to {
        Modifier
    }
}

// Note: When the user closes the side menu, we don't immediately stop rendering it (at which point it would disappear
// abruptly). Instead, we start animating it out and only stop rendering it when the animation is complete.
enum class SideMenuState {
    CLOSED,
    OPEN,
    CLOSING;

    fun close() = when (this) {
        CLOSED -> CLOSED
        OPEN -> CLOSING
        CLOSING -> CLOSING
    }
}

@Composable
fun NavHeader() {
    Row(NavHeaderStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
        Brand()

        Spacer()

        Row(Modifier.gap(1.5.cssRem).displayIfAtLeast(Breakpoint.MD), verticalAlignment = Alignment.CenterVertically) {
            MenuItems()
            LangSwitchButton()
            ColorModeButton()
        }

        Row(
            Modifier
                .fontSize(1.5.cssRem)
                .gap(1.cssRem)
                .displayUntil(Breakpoint.MD),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var menuState by remember { mutableStateOf(SideMenuState.CLOSED) }

            LangSwitchButton()
            ColorModeButton()
            HamburgerButton(onClick =  { menuState = SideMenuState.OPEN })

            if (menuState != SideMenuState.CLOSED) {
                SideMenu(
                    menuState,
                    close = { menuState = menuState.close() },
                    onAnimationEnd = { if (menuState == SideMenuState.CLOSING) menuState = SideMenuState.CLOSED }
                )
            }
        }
    }
}

@Composable
private fun SideMenu(menuState: SideMenuState, close: () -> Unit, onAnimationEnd: () -> Unit) {
    Overlay(
        Modifier
            .setVariable(OverlayVars.BackgroundColor, Colors.Transparent)
            .onClick { close() }
    ) {
        key(menuState) { // Force recompute animation parameters when close button is clicked
            Column(
                Modifier
                    .fillMaxHeight()
                    .width(clamp(8.cssRem, 33.percent, 10.cssRem))
                    .align(Alignment.CenterEnd)
                    // Close button will appear roughly over the hamburger button, so the user can close
                    // things without moving their finger / cursor much.
                    .padding(top = 1.cssRem, leftRight = 1.cssRem)
                    .gap(1.5.cssRem)
                    .backgroundColor(ColorMode.current.toSitePalette().nearBackground)
                    .animation(
                        SideMenuSlideInAnim.toAnimation(
                            duration = 200.ms,
                            timingFunction = if (menuState == SideMenuState.OPEN) AnimationTimingFunction.EaseOut else AnimationTimingFunction.EaseIn,
                            direction = if (menuState == SideMenuState.OPEN) AnimationDirection.Normal else AnimationDirection.Reverse,
                            fillMode = AnimationFillMode.Forwards
                        )
                    )
                    .borderRadius(topLeft = 2.cssRem)
                    .onClick { it.stopPropagation() }
                    .onAnimationEnd { onAnimationEnd() },
                horizontalAlignment = Alignment.End
            ) {
                CloseButton(onClick = { close() })
                Column(Modifier.padding(right = 0.75.cssRem).gap(1.5.cssRem).fontSize(1.4.cssRem), horizontalAlignment = Alignment.End) {
                    MenuItems()
                }
            }
        }
    }
}
