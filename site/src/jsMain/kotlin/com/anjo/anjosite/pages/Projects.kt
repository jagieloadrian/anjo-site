package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.Lang
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.ProjectCard
import com.anjo.anjosite.components.widgets.ProjectSummary

// Real project content, ported from docs/handoff/index.html's PROJECTS screen (specs/004-pages),
// now fetched at runtime from projects.json (request #2) instead of living as an in-source List —
// same manual JSON.parse<dynamic> pattern as trophies.json (research.md §3/§4), no
// kotlinx.serialization dependency.
//
// Per clarification Q1, every project (not just the mock's "featured" one) gets its own
// /projects/{slug} detail route. Static export still needs each slug pre-registered via
// site/build.gradle.kts's addExtraRoute (Kobweb can't discover dynamic routes from a runtime
// fetch) — adding a project to projects.json is enough for content edits, but a brand-new slug
// still needs one line added there too.
data class ProjectEntry(
    val slug: String,
    val kind: String,
    val title: BilingualString,
    val shortDescription: BilingualString,
    val fullDescription: BilingualString,
    val tags: List<String>,
    val coverImageUrl: String? = null,
    val coverImageAlt: BilingualString,
    val repoUrl: String? = null,
    val platform: String,
    val status: BilingualString,
    val role: String = "solo",
    val packageOrRepo: String? = null,
)

fun ProjectEntry.toSummary(lang: Lang, index: Int) = ProjectSummary(
    index = (index + 1).toString().padStart(2, '0'),
    kind = kind,
    title = title(lang),
    description = shortDescription(lang),
    tags = tags,
    href = "/projects/$slug",
)

sealed interface ProjectsFetchState {
    data object Loading : ProjectsFetchState
    data class Loaded(val entries: List<ProjectEntry>) : ProjectsFetchState
    data object Failed : ProjectsFetchState
}

private fun bilingual(json: dynamic): BilingualString = BilingualString(en = json.en as String, pl = json.pl as String)

fun parseProjectEntries(text: String): List<ProjectEntry> {
    val json = kotlin.js.JSON.parse<dynamic>(text)
    return (json.projects as Array<dynamic>).map {
        ProjectEntry(
            slug = it.slug as String,
            kind = it.kind as String,
            title = bilingual(it.title),
            shortDescription = bilingual(it.shortDescription),
            fullDescription = bilingual(it.fullDescription),
            tags = (it.tags as Array<String>).toList(),
            coverImageUrl = it.coverImageUrl as String?,
            coverImageAlt = bilingual(it.coverImageAlt),
            repoUrl = it.repoUrl as String?,
            platform = it.platform as String,
            status = bilingual(it.status),
            role = it.role as String,
            packageOrRepo = it.packageOrRepo as String?,
        )
    }
}

suspend fun fetchProjectEntries(): ProjectsFetchState = try {
    val response = window.fetch("/projects.json").await()
    if (!response.ok) throw Exception("HTTP ${response.status}")
    ProjectsFetchState.Loaded(parseProjectEntries(response.text().await()))
} catch (t: Throwable) {
    ProjectsFetchState.Failed
}

private val LoadingLabel = BilingualString(en = "Loading projects…", pl = "Wczytywanie projektów…")
private val ErrorLabel = BilingualString(
    en = "Projects couldn't be loaded right now.",
    pl = "Nie udało się teraz wczytać projektów.",
)

private val Description = BilingualString(
    en = "Side projects and tools by Adrian Jagieło — Kotlin, Compose Multiplatform, and small utilities.",
    pl = "Własne projekty i narzędzia Adriana Jagieły — Kotlin, Compose Multiplatform i małe narzędzia.",
)

@InitRoute
fun initProjectsPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Projects", Description))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun ProjectsPage() {
    val lang = LocalLang.current
    var fetchState by remember { mutableStateOf<ProjectsFetchState>(ProjectsFetchState.Loading) }

    LaunchedEffect(Unit) {
        fetchState = fetchProjectEntries()
    }

    Section(attrs = { classes("band", "band--strong", "band--pad") }) {
        Div(attrs = { classes("kicker"); style { property("margin-bottom", "18px") } }) {
            val count = (fetchState as? ProjectsFetchState.Loaded)?.entries?.size
            Text(if (count != null) "03 / PROJECTS · $count ENTRIES" else "03 / PROJECTS")
        }
        H1(attrs = { classes("display") }) {
            Text("THINGS I")
            Br()
            Em { Text("BUILT") }
        }
    }

    when (val state = fetchState) {
        is ProjectsFetchState.Loading -> {
            Section(attrs = { classes("band", "band--pad") }) {
                P(attrs = { classes("body") }) { Text(LoadingLabel(lang)) }
            }
        }
        is ProjectsFetchState.Failed -> {
            Section(attrs = { classes("band", "band--pad") }) {
                P(attrs = { classes("body") }) { Text(ErrorLabel(lang)) }
            }
        }
        is ProjectsFetchState.Loaded -> {
            Section(attrs = { classes("cards") }) {
                state.entries.forEachIndexed { index, entry ->
                    ProjectCard(entry.toSummary(lang, index))
                }
            }
        }
    }
}
