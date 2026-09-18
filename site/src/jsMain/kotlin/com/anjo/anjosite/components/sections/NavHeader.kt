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
import com.anjo.anjosite.LocalTheme
import com.anjo.anjosite.LocalThemeSetter
import com.anjo.anjosite.Theme

private val navLinkVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)

private const val BrandHoverColor = "rgba(37, 99, 235, 0.22)"

@Composable
private fun NavLink(path: String, label: String, isActive: Boolean, number: String? = null) {
    val text = if (number != null) "$number / $label" else label
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
            NavLink("/", "Home", path == "/", "01")
            NavLink("/about", "About", path == "/about", "02")
            NavLink("/projects", "Projects", path.startsWith("/projects"), "03")
            NavLink("/trophies", "Trophies", path == "/trophies", "04")
            NavLink("/contact", "Contact", path == "/contact", "05")
            NavLink("/cv", "CV", path == "/cv")
            Button(attrs = {
                classes("theme-btn")
                attr("title", "Switch theme")
                onClick { setTheme(theme.other) }
            }) { Text(if (theme == Theme.DARK) "☀ light" else "☾ dark") }
        }
    }
}
