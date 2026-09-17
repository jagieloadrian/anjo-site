package com.anjo.anjosite.pages.projects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.functions.clamp
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.TouchTargetStyle
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.Tag
import com.anjo.anjosite.components.widgets.TagVariant
import com.anjo.anjosite.pages.projectEntries
import com.varabyte.kobweb.silk.style.toModifier

// Dynamic detail route (@Page("{}") -> /projects/{slug}, research.md §1/§2): one static page per
// project slug once site/build.gradle.kts's extraRoutes registers each one (T003) — otherwise
// `kobweb export` silently skips this route entirely for every project.

@InitRoute
fun initProjectSlugPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Project"))
}

@Page("{}")
@Layout(".components.layouts.PageLayout")
@Composable
fun SlugPage() {
    val ctx = rememberPageContext()
    val lang = LocalLang.current
    val slug = ctx.route.params["slug"]
    val project = projectEntries.find { it.slug == slug }

    if (project == null) {
        LaunchedEffect(slug) {
            ctx.router.navigateTo("/404")
        }
        return
    }

    Column(Modifier.gap(1.5.cssRem)) {
        Link("/projects", "← projects", modifier = TouchTargetStyle.toModifier())
        SpanText(
            project.title(lang),
            Modifier.fontFamily("Archivo", "system-ui", "sans-serif")
                .fontWeight(800)
                .fontSize(clamp(1.5.cssRem, 4.vw, 3.cssRem)),
        )
        SpanText(project.fullDescription(lang), Modifier.fontSize(1.cssRem))
        Row(Modifier.flexWrap(FlexWrap.Wrap).gap(0.5.cssRem)) {
            project.tags.forEach { tag ->
                Tag(tag, TagVariant.TECH_STACK, "/projects")
            }
        }
        project.repoUrl?.let { url ->
            Link(url, "view ↗", modifier = TouchTargetStyle.toModifier())
        }
    }
}
