package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
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
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.vw
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.BilingualTimelineItem
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.resolve
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.TimelineEntry

// Real, print-friendly CV content ported from docs/handoff/index.html's CV screen
// (specs/004-pages FR-012) — its own dedicated data structure, distinct from Projects/About.

data class CvSection(val heading: BilingualString, val entries: List<BilingualTimelineItem>)
data class CvContent(val sections: List<CvSection>)

private val cvContent = CvContent(
    sections = listOf(
        CvSection(
            heading = BilingualString(en = "Experience", pl = "Doświadczenie"),
            entries = listOf(
                BilingualTimelineItem(
                    date = BilingualString(en = "01.2023 — present", pl = "01.2023 — obecnie"),
                    title = BilingualString(
                        en = "Software Developer · GFT Poland Sp. z o.o. · Warsaw",
                        pl = "Software Developer · GFT Poland Sp. z o.o. · Warszawa",
                    ),
                    description = BilingualString(
                        en = "Develop and maintain backend solutions using Java; conduct code reviews and mentor team members; participate in deployment and release processes; collaborate closely with Product Owners, Architects, QA and frontend teams; support onboarding of new team members; participate in client meetings to refine requirements; client project running on OpenShift — hands-on Kubernetes-based deployment and orchestration. Stack: Java · GraphQL · JUnit · Mockito · Jenkins · OpenShift.",
                        pl = "Tworzę i utrzymuję rozwiązania backendowe w Javie; prowadzę code review i mentoring zespołu; uczestniczę w procesach wdrożeń i wydań; ściśle współpracuję z Product Ownerami, Architektami, QA i zespołami frontendowymi; wspieram onboarding nowych osób; uczestniczę w spotkaniach z klientem doprecyzowujących wymagania; projekt klienta na OpenShift — praktyczne wdrażanie i orkiestracja oparta na Kubernetesie. Stack: Java · GraphQL · JUnit · Mockito · Jenkins · OpenShift.",
                    ),
                ),
                BilingualTimelineItem(
                    date = BilingualString(en = "10.2021 — 12.2022", pl = "10.2021 — 12.2022"),
                    title = BilingualString(
                        en = "Junior Software Developer · GFT Poland Sp. z o.o. · Warsaw",
                        pl = "Junior Software Developer · GFT Poland Sp. z o.o. · Warszawa",
                    ),
                    description = BilingualString(
                        en = "Designed and implemented microservices in Kotlin for Kubernetes; built RESTful APIs and maintained technical documentation; supported application deployment processes; collaborated with Product Owners, QA and DevOps teams; participated in requirements gathering and technical discussions with clients. Stack: Kotlin coroutines · MockWebServer · Docker · Kubernetes.",
                        pl = "Projektowałem i wdrażałem mikroserwisy w Kotlinie dla Kubernetesa; budowałem API RESTful i utrzymywałem dokumentację techniczną; wspierałem procesy wdrażania aplikacji; współpracowałem z Product Ownerami, QA i zespołami DevOps; uczestniczyłem w zbieraniu wymagań i rozmowach technicznych z klientami. Stack: Kotlin coroutines · MockWebServer · Docker · Kubernetes.",
                    ),
                ),
            ),
        ),
        CvSection(
            heading = BilingualString(en = "Own Projects", pl = "Własne projekty"),
            entries = listOf(
                BilingualTimelineItem(
                    date = BilingualString(en = "", pl = ""),
                    title = BilingualString(en = "SW Wiki", pl = "SW Wiki"),
                    description = BilingualString(
                        en = "Mobile app fetching Star Wars data from external APIs, including image search by keywords. On Google Play. Stack: Apollo · Jetpack Compose · Gradle · Kotlin.",
                        pl = "Aplikacja mobilna pobierająca dane o Gwiezdnych Wojnach z zewnętrznych API, wraz z wyszukiwaniem obrazów po słowach kluczowych. Dostępna w Google Play. Stack: Apollo · Jetpack Compose · Gradle · Kotlin.",
                    ),
                ),
                BilingualTimelineItem(
                    date = BilingualString(en = "", pl = ""),
                    title = BilingualString(en = "DatabaseSchedulerExecutor", pl = "DatabaseSchedulerExecutor"),
                    description = BilingualString(
                        en = "Kotlin app that executes SQLite queries at scheduled times based on CRON expressions, configurable via a properties file. Stack: Kotlin · JDBC SQLite · CRON.",
                        pl = "Aplikacja w Kotlinie wykonująca zapytania SQLite według harmonogramu z wyrażeń CRON, konfigurowalna przez plik properties. Stack: Kotlin · JDBC SQLite · CRON.",
                    ),
                ),
            ),
        ),
        CvSection(
            heading = BilingualString(en = "Skills", pl = "Umiejętności"),
            entries = listOf(
                BilingualTimelineItem(
                    date = BilingualString(en = "", pl = ""),
                    title = BilingualString(en = "Backend", pl = "Backend"),
                    description = BilingualString(en = "Kotlin, Java, Spring, GraphQL, Coroutines", pl = "Kotlin, Java, Spring, GraphQL, Coroutines"),
                ),
                BilingualTimelineItem(
                    date = BilingualString(en = "", pl = ""),
                    title = BilingualString(en = "Testing", pl = "Testowanie"),
                    description = BilingualString(en = "JUnit, Mockito, MockWebServer, Cucumber", pl = "JUnit, Mockito, MockWebServer, Cucumber"),
                ),
                BilingualTimelineItem(
                    date = BilingualString(en = "", pl = ""),
                    title = BilingualString(en = "DevOps / Cloud", pl = "DevOps / Chmura"),
                    description = BilingualString(en = "Docker, Kubernetes, OpenShift, Jenkins", pl = "Docker, Kubernetes, OpenShift, Jenkins"),
                ),
                BilingualTimelineItem(
                    date = BilingualString(en = "", pl = ""),
                    title = BilingualString(en = "Other", pl = "Inne"),
                    description = BilingualString(
                        en = "RESTful API design, microservices architecture",
                        pl = "Projektowanie API RESTful, architektura mikroserwisów",
                    ),
                ),
            ),
        ),
        CvSection(
            heading = BilingualString(en = "Profile", pl = "Profil"),
            entries = listOf(
                BilingualTimelineItem(
                    date = BilingualString(en = "", pl = ""),
                    title = BilingualString(en = "Languages", pl = "Języki"),
                    description = BilingualString(
                        en = "Polish — native, English — professional",
                        pl = "Polski — ojczysty, Angielski — profesjonalny",
                    ),
                ),
                BilingualTimelineItem(
                    date = BilingualString(en = "", pl = ""),
                    title = BilingualString(en = "Interests", pl = "Zainteresowania"),
                    description = BilingualString(
                        en = "History of video games, Motorcycles, Cooking",
                        pl = "Historia gier wideo, Motocykle, Gotowanie",
                    ),
                ),
            ),
        ),
    ),
)

@InitRoute
fun initCvPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("CV"))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun CvPage() {
    val lang = LocalLang.current

    Column(Modifier.gap(2.cssRem)) {
        SpanText(
            "ADRIAN JAGIEŁO",
            Modifier
                .fontFamily("Archivo", "system-ui", "sans-serif")
                .fontWeight(900)
                .fontSize(clamp(1.75.cssRem, 4.vw, 3.cssRem)),
        )
        SpanText("Kotlin | Java Developer", Modifier.fontSize(1.125.cssRem))
        Row(Modifier.gap(1.cssRem).flexWrap(FlexWrap.Wrap)) {
            Link("mailto:jagielo.adrian@gmail.com", "jagielo.adrian@gmail.com")
            Link("https://www.linkedin.com/in/jagieloadrian/", "linkedin.com/in/jagieloadrian ↗")
            Link("https://github.com/jagieloadrian", "github.com/jagieloadrian ↗")
            SpanText("Warsaw, Poland")
        }

        cvContent.sections.forEach { section ->
            Column(Modifier.gap(1.cssRem)) {
                SpanText(
                    section.heading(lang),
                    Modifier.fontFamily("JetBrains Mono", "monospace").fontSize(0.875.cssRem),
                )
                Column(Modifier.gap(1.25.cssRem)) {
                    section.entries.forEach { entry ->
                        TimelineEntry(entry.resolve(lang))
                    }
                }
            }
        }
    }
}
