package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.ProjectCard
import com.anjo.anjosite.components.widgets.ProjectSummary
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

data class ProjectEntry(
    val slug: String,
    val kind: String,
    val title: String,
    val shortDescription: String,
    val fullDescription: String,
    val tags: List<String>,
    val coverImageUrl: String? = null,
    val coverImageAlt: String,
    val repoUrl: String? = null,
    val platform: String,
    val status: String,
    val role: String = "solo",
    val packageOrRepo: String? = null,
)

fun ProjectEntry.toSummary(index: Int) = ProjectSummary(
    index = (index + 1).toString().padStart(2, '0'),
    kind = kind,
    title = title,
    description = shortDescription,
    tags = tags,
    href = "/projects/$slug",
)

sealed interface ProjectsFetchState {
    data object Loading : ProjectsFetchState
    data class Loaded(val entries: List<ProjectEntry>) : ProjectsFetchState
    data object Failed : ProjectsFetchState
}

fun parseProjectEntries(text: String): List<ProjectEntry> {
    val json = JSON.parse<dynamic>(text)
    return (json.projects as Array<dynamic>).map {
        ProjectEntry(
            slug = it.slug as String,
            kind = it.kind as String,
            title = it.title as String,
            shortDescription = it.shortDescription as String,
            fullDescription = it.fullDescription as String,
            tags = (it.tags as Array<String>).toList(),
            coverImageUrl = it.coverImageUrl as String?,
            coverImageAlt = it.coverImageAlt as String,
            repoUrl = it.repoUrl as String?,
            platform = it.platform as String,
            status = it.status as String,
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
    console.error(t)
    ProjectsFetchState.Failed
}

private const val LoadingLabel = "Loading projects…"
private const val ErrorLabel = "Projects couldn't be loaded right now."

private const val Description = "Side projects and tools by Adrian Jagieło — Kotlin, Compose Multiplatform, and small utilities."

@InitRoute
fun initProjectsPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Projects", Description))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun ProjectsPage() {
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
                P(attrs = { classes("body") }) { Text(LoadingLabel) }
            }
        }
        is ProjectsFetchState.Failed -> {
            Section(attrs = { classes("band", "band--pad") }) {
                P(attrs = { classes("body") }) { Text(ErrorLabel) }
            }
        }
        is ProjectsFetchState.Loaded -> {
            Section(attrs = { classes("cards") }) {
                state.entries.forEachIndexed { index, entry ->
                    ProjectCard(entry.toSummary(index))
                }
            }
        }
    }
}
