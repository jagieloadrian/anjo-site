package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.clamp
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
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
import org.jetbrains.compose.web.dom.Img
import com.anjo.anjosite.toSitePalette

data class ProjectSummary(
    val title: String,
    val description: String,
    val tags: List<String>,
    val coverImageUrl: String?,
    val coverImageAlt: String,
    val href: String,
)

// Whole-card Link, tokens/typography/touch-target per FR-010/011/012 (research.md §2/§3).
val ProjectCardStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .flexDirection(FlexDirection.Column)
            .gap(0.75.cssRem)
            .padding(2.cssRem, 1.5.cssRem)
    }
    (CssRule.OfMedia(CSSMediaQuery.MediaFeature("max-width", 720.px))) {
        Modifier.minHeight(48.px)
    }
}

val ProjectCoverStyle = CssStyle.base {
    Modifier.fillMaxWidth().height(10.cssRem).borderRadius(0.5.cssRem)
}

val ProjectTitleStyle = CssStyle.base {
    Modifier
        .fontFamily("Archivo", "system-ui", "sans-serif")
        .fontWeight(800)
        .fontSize(clamp(1.125.cssRem, 2.5.vw, 1.5.cssRem))
}

val ProjectDescriptionStyle = CssStyle.base {
    Modifier.fontSize(clamp(0.875.cssRem, 1.5.vw, 1.cssRem))
}

val ProjectTagChipStyle = CssStyle.base {
    Modifier
        .fontFamily("JetBrains Mono", "monospace")
        .fontSize(clamp(0.625.cssRem, 1.vw, 0.6875.cssRem))
        .padding(leftRight = 0.5.cssRem, topBottom = 0.25.cssRem)
        .borderRadius(999.px)
}

@Composable
private fun ProjectCover(url: String?, alt: String, backgroundColor: Modifier) {
    if (url != null) {
        Img(url, alt, attrs = ProjectCoverStyle.toModifier().toAttrs())
    } else {
        Box(ProjectCoverStyle.toModifier().then(backgroundColor))
    }
}

@Composable
fun ProjectCard(project: ProjectSummary) {
    val sitePalette = ColorMode.current.toSitePalette()
    Link(
        project.href,
        modifier = ProjectCardStyle.toModifier(),
        variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
    ) {
        Column(Modifier.fillMaxWidth().gap(0.75.cssRem)) {
            ProjectCover(
                project.coverImageUrl,
                project.coverImageAlt,
                Modifier.backgroundColor(sitePalette.nearBackground)
            )
            SpanText(project.title, ProjectTitleStyle.toModifier())
            SpanText(project.description, ProjectDescriptionStyle.toModifier().color(sitePalette.ink.toRgb().copyf(alpha = 0.85f)))
            Row(Modifier.flexWrap(FlexWrap.Wrap).gap(0.5.cssRem)) {
                project.tags.forEach { tag ->
                    SpanText(
                        tag,
                        ProjectTagChipStyle.toModifier()
                            .color(sitePalette.cyan)
                            .backgroundColor(sitePalette.cyan.toRgb().copyf(alpha = 0.1f))
                    )
                }
            }
        }
    }
}
