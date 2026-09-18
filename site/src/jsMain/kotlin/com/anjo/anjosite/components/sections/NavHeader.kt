package com.anjo.anjosite.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.dataAttr
import com.varabyte.kobweb.compose.ui.modifiers.onMouseEnter
import com.varabyte.kobweb.compose.ui.modifiers.onMouseLeave
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Text
import com.anjo.anjosite.AboutLabel
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.ContactLabel
import com.anjo.anjosite.CvLabel
import com.anjo.anjosite.HomeLabel
import com.anjo.anjosite.Lang
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.LocalLangSetter
import com.anjo.anjosite.LocalTheme
import com.anjo.anjosite.LocalThemeSetter
import com.anjo.anjosite.ProjectsLabel
import com.anjo.anjosite.Theme
import com.anjo.anjosite.TrophiesLabel

// Literal port of docs/handoff/index.html's <header class="nav"> — no hamburger/side-menu (the
// mock's own mobile answer is letting .nav-group scroll horizontally, styles.css:358-371) and no
// "design notes" button (explicitly "drop this block on the live site" per app.js). Light/dark
// toggle IS ported (request #7, mock's own recent addition).
private val navLinkVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)

// Request #6, a deliberate deviation from the mock (which has no blue token): translucent blue
// glow behind the brand on hover, on top of the mock's own .glitch shake (styles.css's
// `.glitch:hover` animation — just needs the class + data-text here to fire). Plain
// onMouseEnter/onMouseLeave + inline style, not a Silk CssStyle: Kobweb wraps CssStyle output in
// an unconditional `@layer`, and CSS gives ANY unlayered rule priority over ANY layered one
// regardless of specificity — so a CssStyle-based hover can never beat the mock's plain
// (unlayered) `.brand { background: transparent }` in styles.css. Verified in-browser.
private const val BrandHoverColor = "rgba(37, 99, 235, 0.22)"

@Composable
private fun NavLink(path: String, label: BilingualString, isActive: Boolean, number: String? = null) {
    val text = label(LocalLang.current).let { if (number != null) "$number / $it" else it }
    Link(
        path,
        text,
        Modifier.classNames("nav-btn").let { if (isActive) it.classNames("is-active") else it },
        variant = navLinkVariant,
    )
}

@Composable
fun NavHeader() {
    val ctx = rememberPageContext()
    val path = ctx.route.path
    val lang = LocalLang.current
    val setLang = LocalLangSetter.current
    val theme = LocalTheme.current
    val setTheme = LocalThemeSetter.current

    var brandHovered by remember { mutableStateOf(false) }

    Div(attrs = { classes("nav") }) {
        Link(
            "/",
            Modifier.classNames("brand", "glitch")
                .dataAttr("text", "ADRIAN JAGIEŁO")
                .onMouseEnter { brandHovered = true }
                .onMouseLeave { brandHovered = false }
                .styleModifier { property("background", if (brandHovered) BrandHoverColor else "transparent") },
            variant = navLinkVariant,
        ) {
            I {}
            Text("ADRIAN JAGIEŁO")
        }
        Div(attrs = { classes("nav-group") }) {
            NavLink("/", HomeLabel, path == "/", "01")
            NavLink("/about", AboutLabel, path == "/about", "02")
            NavLink("/projects", ProjectsLabel, path.startsWith("/projects"), "03")
            NavLink("/trophies", TrophiesLabel, path == "/trophies", "04")
            NavLink("/contact", ContactLabel, path == "/contact", "05")
            NavLink("/cv", CvLabel, path == "/cv")
            Div(attrs = { classes("lang") }) {
                org.jetbrains.compose.web.dom.Button(attrs = {
                    classes("lang-btn")
                    if (lang == Lang.EN) classes("is-active")
                    onClick { setLang(Lang.EN) }
                }) { Text("EN") }
                org.jetbrains.compose.web.dom.Button(attrs = {
                    classes("lang-btn")
                    if (lang == Lang.PL) classes("is-active")
                    onClick { setLang(Lang.PL) }
                }) { Text("PL") }
            }
            Button(attrs = {
                classes("theme-btn")
                attr("title", "Switch theme")
                onClick { setTheme(theme.other) }
            }) { Text(if (theme == Theme.DARK) "☀ light" else "☾ dark") }
        }
    }
}
