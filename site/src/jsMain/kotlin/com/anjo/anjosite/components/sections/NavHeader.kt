package com.anjo.anjosite.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
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
import com.anjo.anjosite.ProjectsLabel
import com.anjo.anjosite.TrophiesLabel

// Literal port of docs/handoff/index.html's <header class="nav"> — no hamburger/side-menu (the
// mock's own mobile answer is letting .nav-group scroll horizontally, styles.css:358-371) and no
// light/dark toggle or "design notes" button (mock has one theme; design notes are explicitly
// "drop this block on the live site" per app.js).
private val navLinkVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)

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

    Div(attrs = { classes("nav") }) {
        Link("/", Modifier.classNames("brand"), variant = navLinkVariant) {
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
        }
    }
}
