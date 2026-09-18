package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.Fact
import com.anjo.anjosite.components.widgets.TimelineEntry
import com.anjo.anjosite.components.widgets.TimelineItem

private const val Lead = "I build backend systems in Kotlin and Java and I like the part of the job most people skip: making the thing understandable afterwards. Documentation, code review, onboarding, explaining a design to a product owner in language they can act on."
private const val Body1 = "Day to day that means microservices deployed on Kubernetes and OpenShift, REST and GraphQL APIs, and release processes I help keep boring. Currently on a client project at GFT Poland, where I also mentor team members and help new developers get productive."
private const val Body2 = "The side projects are where I try things the day job has no room for — Compose, multiplatform, small tools that solve exactly one problem I had."

private val timelineEntries = listOf(
    TimelineItem("01.2023 — PRESENT", "Software Developer", "GFT Poland · Warsaw", "Java · GraphQL · JUnit · Mockito · Jenkins · OpenShift") to
        "Backend in Java, code review and mentoring, releases, client requirement sessions. Client project on OpenShift.",
    TimelineItem("10.2021 — 12.2022", "Junior Software Developer", "GFT Poland · Warsaw", "Kotlin coroutines · MockWebServer · Docker · Kubernetes") to
        "Designed and implemented Kotlin microservices for Kubernetes, built REST APIs and kept the technical documentation current.",
)

private val navLinkVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)

private const val Description = "I build backend systems in Kotlin and Java, and care most about making them understandable afterwards."

@InitRoute
fun initAboutPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("About", Description))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun AboutPage() {
    Section(attrs = { classes("band", "band--strong", "band--pad") }) {
        Div(attrs = { classes("kicker"); style { property("margin-bottom", "18px") } }) { Text("02 / ABOUT") }
        H1(attrs = { classes("display") }) {
            Text("WHO IS")
            Br()
            Em(attrs = { classes("cyan") }) { Text("BEHIND THIS") }
        }
    }

    Section(attrs = { classes("band", "split") }) {
        Div {
            P(attrs = { classes("lead") }) { Text(Lead) }
            P(attrs = { classes("body") }) { Text(Body1) }
            P(attrs = { classes("body"); style { property("margin-bottom", "0") } }) { Text(Body2) }
            Link(
                "/cv", "full cv →",
                Modifier.classNames("btn").styleModifier { property("margin-top", "32px") },
                variant = navLinkVariant,
            )
        }
        Div {
            Div(attrs = { classes("label", "label--sm"); style { property("margin-bottom", "20px") } }) { Text("TIMELINE") }
            timelineEntries.forEachIndexed { index, (item, description) ->
                TimelineEntry(item, soft = index > 0) {
                    P(attrs = {
                        classes("body", "body--sm")
                        style { property("max-width", "46ch"); property("margin", "10px 0 0"); property("font-size", "14px") }
                    }) { Text(description) }
                }
            }
            Div(attrs = { classes("label", "label--sm"); style { property("margin", "40px 0 16px") } }) { Text("OFF THE CLOCK") }
            Div(attrs = { classes("facts") }) {
                Fact("Video games", meta = "+ their history")
                Fact("Motorcycles")
                Fact("Cooking")
            }
        }
    }
}
