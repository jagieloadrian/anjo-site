package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.gridTemplateColumns
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.Lang
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.ProjectCard
import com.anjo.anjosite.components.widgets.ProjectSummary

// Real project content ported from docs/handoff/index.html's PROJECTS screen (specs/004-pages).
// Per clarification Q1, every project (not just the mock's "featured" one) gets its own
// /projects/{slug} detail route — a deliberate simplification of the mock's mixed
// detail-page/straight-to-repo pattern, for a single consistent architecture.
data class ProjectEntry(
    val slug: String,
    val title: BilingualString,
    val shortDescription: BilingualString,
    val fullDescription: BilingualString,
    val tags: List<String>,
    val coverImageUrl: String? = null,
    val coverImageAlt: BilingualString,
    val repoUrl: String? = null,
)

fun ProjectEntry.toSummary(lang: Lang) = ProjectSummary(
    title = title(lang),
    description = shortDescription(lang),
    tags = tags,
    coverImageUrl = coverImageUrl,
    coverImageAlt = coverImageAlt(lang),
    href = "/projects/$slug",
)

// Named `projectEntries`, not `projects` — this file's own package is `com.anjo.anjosite.pages`,
// but the dynamic detail route lives in the sub-package `com.anjo.anjosite.pages.projects`
// (required so its URL prefix is `/projects/...`); a top-level `projects` property here would
// collide with that sub-package's fully-qualified name.
val projectEntries: List<ProjectEntry> = listOf(
    ProjectEntry(
        slug = "star-wars-wiki-compose",
        title = BilingualString(en = "Star Wars Wiki Compose", pl = "Star Wars Wiki Compose"),
        shortDescription = BilingualString(
            en = "My first project shipped to the world: Star Wars data from external APIs plus keyword image search, on Google Play.",
            pl = "Mój pierwszy projekt udostępniony światu: dane o Gwiezdnych Wojnach z zewnętrznych API oraz wyszukiwanie obrazów po słowach kluczowych, na Google Play.",
        ),
        fullDescription = BilingualString(
            en = "My first big project, and the first one I put in front of strangers. It pulls Star Wars data from public APIs and adds keyword image search on top, so a character page arrives with pictures instead of a blank slot. The UI is Jetpack Compose end to end; data comes through Apollo, so the schema drives the models rather than hand-written DTOs. Published on the Play Store under the diether18 developer account. What I took from it: Compose state handling on lists that load from two sources at once, keeping API keys out of a client app that ships publicly, and the release process on Google Play from signing to store listing.",
            pl = "Mój pierwszy duży projekt i pierwszy, który pokazałem obcym ludziom. Pobiera dane o Gwiezdnych Wojnach z publicznych API i dodaje do tego wyszukiwanie obrazów po słowach kluczowych, więc strona postaci ma zdjęcia zamiast pustego miejsca. Interfejs to Jetpack Compose od początku do końca; dane płyną przez Apollo, więc to schema napędza modele, a nie ręcznie pisane DTO. Opublikowany w Play Store na koncie deweloperskim diether18. Czego się nauczyłem: obsługa stanu w Compose na listach ładowanych z dwóch źródeł naraz, trzymanie kluczy API z dala od publicznie wydawanej aplikacji klienckiej oraz proces wydania w Google Play od podpisania po wpis w sklepie.",
        ),
        tags = listOf("Kotlin", "Compose", "Apollo"),
        coverImageAlt = BilingualString(en = "Star Wars Wiki Compose app icon", pl = "Ikona aplikacji Star Wars Wiki Compose"),
        repoUrl = "https://play.google.com/store/apps/details?id=com.anjo.starwarswikicompose",
    ),
    ProjectEntry(
        slug = "filebrowser-api",
        title = BilingualString(en = "Filebrowser API", pl = "Filebrowser API"),
        shortDescription = BilingualString(
            en = "A private project born from needing a container to manage files for another service. Spring and Kotlin.",
            pl = "Prywatny projekt powstały z potrzeby kontenera do zarządzania plikami dla innej usługi. Spring i Kotlin.",
        ),
        fullDescription = BilingualString(
            en = "A private project born from needing a container to manage files for another service — a small Spring Boot service in Kotlin, packaged as a Docker container, exposing a minimal file-management API.",
            pl = "Prywatny projekt powstały z potrzeby kontenera do zarządzania plikami dla innej usługi — mała usługa Spring Boot w Kotlinie, spakowana jako kontener Docker, udostępniająca minimalne API do zarządzania plikami.",
        ),
        tags = listOf("Kotlin", "Spring", "Docker"),
        coverImageAlt = BilingualString(en = "Filebrowser API", pl = "Filebrowser API"),
        repoUrl = "https://github.com/jagieloadrian",
    ),
    ProjectEntry(
        slug = "database-scheduler-executor",
        title = BilingualString(en = "Database Scheduler Executor", pl = "Database Scheduler Executor"),
        shortDescription = BilingualString(
            en = "Kotlin app that runs SQLite statements on a schedule from CRON expressions, configured in a properties file.",
            pl = "Aplikacja w Kotlinie uruchamiająca zapytania SQLite według harmonogramu z wyrażeń CRON, konfigurowana w pliku properties.",
        ),
        fullDescription = BilingualString(
            en = "A Kotlin app that executes SQLite statements on a schedule driven by CRON expressions, with the schedule and queries configured entirely through a properties file — no code changes needed to add a new scheduled job.",
            pl = "Aplikacja w Kotlinie wykonująca zapytania SQLite według harmonogramu opartego na wyrażeniach CRON, gdzie harmonogram i zapytania konfiguruje się w całości przez plik properties — dodanie nowego zaplanowanego zadania nie wymaga zmian w kodzie.",
        ),
        tags = listOf("Kotlin", "JDBC SQLite", "CRON"),
        coverImageAlt = BilingualString(en = "Database Scheduler Executor", pl = "Database Scheduler Executor"),
        repoUrl = "https://github.com/jagieloadrian",
    ),
    ProjectEntry(
        slug = "tuya-key-extractor-kt",
        title = BilingualString(en = "Tuya Key Extractor KT", pl = "Tuya Key Extractor KT"),
        shortDescription = BilingualString(
            en = "Local key extractor for Tuya devices. Written because I needed it to run somewhere other than Windows.",
            pl = "Lokalny ekstraktor kluczy dla urządzeń Tuya. Napisany, bo potrzebowałem, żeby działał gdzieś poza Windowsem.",
        ),
        fullDescription = BilingualString(
            en = "A local key extractor for Tuya smart-home devices, written because I needed it to run somewhere other than Windows — the JVM is the most universal engine for that, so a small Kotlin/JVM tool it is.",
            pl = "Lokalny ekstraktor kluczy dla urządzeń Tuya smart-home, napisany dlatego, że potrzebowałem, żeby działał gdzieś poza Windowsem — JVM jest do tego najbardziej uniwersalnym silnikiem, więc powstało małe narzędzie w Kotlin/JVM.",
        ),
        tags = listOf("Kotlin", "JVM"),
        coverImageAlt = BilingualString(en = "Tuya Key Extractor KT", pl = "Tuya Key Extractor KT"),
        repoUrl = "https://github.com/jagieloadrian",
    ),
)

val ProjectsGridStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .display(DisplayStyle.Grid)
        .styleModifier { gridTemplateColumns("repeat(auto-fit, minmax(320px, 1fr))") }
        .gap(2.cssRem)
}

@InitRoute
fun initProjectsPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Projects"))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun ProjectsPage() {
    val lang = LocalLang.current
    Box(ProjectsGridStyle.toModifier()) {
        projectEntries.forEach { entry ->
            ProjectCard(entry.toSummary(lang))
        }
    }
}
