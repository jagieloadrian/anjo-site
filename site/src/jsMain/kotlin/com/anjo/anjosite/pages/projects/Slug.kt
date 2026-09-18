package com.anjo.anjosite.pages.projects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.layouts.updatePageMeta
import com.anjo.anjosite.components.widgets.Tag
import com.anjo.anjosite.pages.ProjectEntry
import com.anjo.anjosite.pages.ProjectsFetchState
import com.anjo.anjosite.pages.fetchProjectEntries

// Dynamic detail route (@Page("{}") -> /projects/{slug}, research.md §1/§2): one static page per
// project slug once site/build.gradle.kts's extraRoutes registers each one (T003) — otherwise
// `kobweb export` silently skips this route entirely for every project. Literal port of
// docs/handoff/index.html's data-screen="project" template. Content is fetched from
// projects.json (request #2), same as the grid in pages/Projects.kt.

private val navLinkVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)

private val LoadingLabel = BilingualString(en = "Loading project…", pl = "Wczytywanie projektu…")
private val ErrorLabel = BilingualString(
    en = "This project couldn't be loaded right now.",
    pl = "Nie udało się teraz wczytać tego projektu.",
)
private val Description = BilingualString(
    en = "A side project by Adrian Jagieło.",
    pl = "Własny projekt Adriana Jagieły.",
)

// specs/007-tests-polishing data-model.md: the real per-project title/description/og:image can't
// be known at static @InitRoute time (it depends on the runtime projects.json fetch below) — this
// static default is what a crawler sees if the export snapshot is somehow taken before the fetch
// resolves; SlugPage()'s own LaunchedEffect(project) overrides it once the real data is in.
@InitRoute
fun initProjectSlugPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Project", Description))
}

// specs/007-tests-polishing research.md §3: extracted so the unknown-slug -> /404 fallback is a
// pure, testable function instead of inline .find{}.
internal fun findProjectBySlug(entries: List<ProjectEntry>, slug: String?): ProjectEntry? =
    entries.find { it.slug == slug }

@Page("{}")
@Layout(".components.layouts.PageLayout")
@Composable
fun SlugPage() {
    val ctx = rememberPageContext()
    val lang = LocalLang.current
    val slug = ctx.route.params["slug"]
    var fetchState by remember { mutableStateOf<ProjectsFetchState>(ProjectsFetchState.Loading) }

    LaunchedEffect(Unit) {
        fetchState = fetchProjectEntries()
    }

    val state = fetchState
    if (state is ProjectsFetchState.Loading) {
        Section(attrs = { classes("band", "band--pad") }) {
            P(attrs = { classes("body") }) { Text(LoadingLabel(lang)) }
        }
        return
    }
    if (state is ProjectsFetchState.Failed) {
        Section(attrs = { classes("band", "band--pad") }) {
            P(attrs = { classes("body") }) { Text(ErrorLabel(lang)) }
        }
        return
    }
    val project = findProjectBySlug((state as ProjectsFetchState.Loaded).entries, slug)

    if (project == null) {
        LaunchedEffect(slug) {
            ctx.router.navigateTo("/404")
        }
        return
    }

    LaunchedEffect(project) {
        updatePageMeta(project.title(lang), project.shortDescription(lang), project.coverImageUrl ?: "/og-banner.png")
    }

    Section(attrs = { classes("band", "band--strong"); style { property("padding", "40px 24px") } }) {
        Link("/projects", "← projects", Modifier.classNames("btn", "btn--link"), variant = navLinkVariant)
        Div(attrs = { classes("kicker"); style { property("margin", "24px 0 16px") } }) {
            Text("${project.kind} · ${project.status(lang).uppercase()}")
        }
        H1(attrs = { classes("display"); style { property("font-size", "clamp(36px, 5.5vw, 80px)"); property("line-height", "0.92") } }) {
            Text(project.title(lang).substringBeforeLast(' ', ""))
            Br()
            Em(attrs = { classes("red") }) { Text(project.title(lang).substringAfterLast(' ')) }
        }
    }

    Section(attrs = { classes("band", "split") }) {
        Div {
            P(attrs = { classes("body") }) { Text(project.fullDescription(lang)) }
            Div(attrs = { classes("tags"); style { property("gap", "0.5rem"); property("margin-top", "1rem") } }) {
                project.tags.forEach { tag -> Tag(tag) }
            }
            project.repoUrl?.let { url ->
                val isStore = url.contains("play.google.com")
                Div(attrs = { classes("tags"); style { property("gap", "12px"); property("margin-top", "32px") } }) {
                    Link(
                        url,
                        if (isStore) "play store ↗" else "repo ↗",
                        Modifier.classNames("btn", "btn--sm", if (isStore) "btn--outline" else "btn--ghost"),
                        variant = navLinkVariant,
                    )
                }
            }
        }
        Div {
            Div(attrs = { classes("sheet") }) {
                Div(attrs = { classes("sheet-head") }) { Text("FACT SHEET") }
                Div(attrs = { classes("sheet-row") }) { Span { Text("platform") }; Span { Text(project.platform) } }
                Div(attrs = { classes("sheet-row") }) {
                    Span { Text("status") }
                    Span(attrs = { style { property("color", "var(--pink)") } }) { Text(project.status(lang)) }
                }
                Div(attrs = { classes("sheet-row") }) { Span { Text("role") }; Span { Text(project.role) } }
                project.packageOrRepo?.let { pkg ->
                    Div(attrs = { classes("sheet-row") }) {
                        Span { Text("package") }
                        Span(attrs = { style { property("text-align", "right"); property("word-break", "break-all") } }) { Text(pkg) }
                    }
                }
            }
            Div(attrs = {
                classes("slot")
                style {
                    property("margin-top", "24px"); property("min-height", "240px")
                    property("display", "flex"); property("flex-direction", "column"); property("justify-content", "center")
                }
            }) {
                Div(attrs = { classes("slot-key") }) { Text("IMAGE SLOT") }
                P { Text("App screenshots go here — 2 or 3 phone captures, exported at the same width.") }
            }
        }
    }
}
