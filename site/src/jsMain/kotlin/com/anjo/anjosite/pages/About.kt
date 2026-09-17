package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.functions.clamp
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.vw
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.BilingualTimelineItem
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.resolve
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.TimelineEntry

// Real About content ported from docs/handoff/index.html's ABOUT screen (specs/004-pages
// FR-003/FR-004) — this route doesn't exist before this phase.

data class AboutContent(val bio: BilingualString, val timeline: List<BilingualTimelineItem>)

private val aboutContent = AboutContent(
    bio = BilingualString(
        en = "I build backend systems in Kotlin and Java and I like the part of the job most " +
            "people skip: making the thing understandable afterwards. Documentation, code " +
            "review, onboarding, explaining a design to a product owner in language they can " +
            "act on. Day to day that means microservices deployed on Kubernetes and OpenShift, " +
            "REST and GraphQL APIs, and release processes I help keep boring. Currently on a " +
            "client project at GFT Poland, where I also mentor team members and help new " +
            "developers get productive. The side projects are where I try things the day job " +
            "has no room for — Compose, multiplatform, small tools that solve exactly one " +
            "problem I had. Outside work: the history of video games, motorcycles, cooking.",
        pl = "Buduję systemy backendowe w Kotlinie i Javie, a najbardziej lubię tę część pracy, " +
            "którą większość pomija: sprawienie, żeby to potem było zrozumiałe. Dokumentacja, " +
            "code review, onboarding, wyjaśnienie rozwiązania product ownerowi w języku, na " +
            "którym może działać. W praktyce: mikroserwisy na Kubernetesie i OpenShifcie, API " +
            "REST i GraphQL oraz procesy wydawnicze, które staram się trzymać nudnymi. Obecnie " +
            "projekt klienta w GFT Poland, gdzie mentoruję zespół i pomagam nowym osobom wejść " +
            "w projekt. Własne projekty to miejsce na rzeczy, na które nie ma miejsca w pracy " +
            "— Compose, multiplatform, małe narzędzia rozwiązujące dokładnie jeden mój problem. " +
            "Po godzinach: historia gier wideo, motocykle, gotowanie.",
    ),
    timeline = listOf(
        BilingualTimelineItem(
            date = BilingualString(en = "01.2023 — present", pl = "01.2023 — obecnie"),
            title = BilingualString(en = "Software Developer", pl = "Software Developer"),
            description = BilingualString(
                en = "GFT Poland · Warsaw — backend in Java, code review and mentoring, releases, client requirement sessions. Client project on OpenShift.",
                pl = "GFT Poland · Warszawa — backend w Javie, code review i mentoring, wydania, sesje wymagań z klientem. Projekt klienta na OpenShift.",
            ),
        ),
        BilingualTimelineItem(
            date = BilingualString(en = "10.2021 — 12.2022", pl = "10.2021 — 12.2022"),
            title = BilingualString(en = "Junior Software Developer", pl = "Junior Software Developer"),
            description = BilingualString(
                en = "GFT Poland · Warsaw — designed and implemented Kotlin microservices for Kubernetes, built REST APIs and kept the technical documentation current.",
                pl = "GFT Poland · Warszawa — projektowałem i wdrażałem mikroserwisy w Kotlinie dla Kubernetesa, budowałem API REST i dbałem o aktualność dokumentacji technicznej.",
            ),
        ),
    ),
)

@InitRoute
fun initAboutPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("About"))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun AboutPage() {
    val lang = LocalLang.current

    Column(Modifier.gap(2.cssRem)) {
        SpanText(
            aboutContent.bio(lang),
            Modifier.fontSize(clamp(1.cssRem, 1.5.vw, 1.125.cssRem)).maxWidth(60.cssRem),
        )
        Column(Modifier.gap(1.5.cssRem)) {
            aboutContent.timeline.forEach { item ->
                TimelineEntry(item.resolve(lang))
            }
        }
    }
}
