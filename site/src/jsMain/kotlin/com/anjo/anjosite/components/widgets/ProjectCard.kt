package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

data class ProjectSummary(
    val index: String,
    val kind: String,
    val title: String,
    val description: String,
    val tags: List<String>,
    val href: String,
)

@Composable
fun ProjectCard(project: ProjectSummary) {
    Div(attrs = { classes("card") }) {
        Div(attrs = { classes("card-top") }) {
            Span(attrs = { classes("card-idx") }) { Text(project.index) }
            Span(attrs = { classes("card-kind") }) { Text(project.kind) }
        }
        Link(
            project.href,
            project.title,
            Modifier.classNames("card-title", "glitch"),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant),
        )
        org.jetbrains.compose.web.dom.P(attrs = { classes("body", "body--sm") }) { Text(project.description) }
        Div(attrs = { classes("tags") }) {
            project.tags.forEach { tag -> Tag(tag, small = true) }
        }
        Link(
            project.href,
            "open case →",
            Modifier.classNames("btn", "btn--link"),
            variant = UndecoratedLinkVariant.then(UncoloredLinkVariant),
        )
    }
}
