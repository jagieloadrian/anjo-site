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
import com.anjo.anjosite.components.layouts.PageLayoutData

private data class CvRole(val title: String, val date: String, val where: String, val bullets: List<String>, val stack: String, val soft: Boolean = false)

private val experience = listOf(
    CvRole(
        "Software Developer", "01.2023 — present", "GFT Poland Sp. z o. o. · Warsaw",
        listOf(
            "Develop and maintain backend solutions using Java",
            "Conduct code reviews and mentor team members",
            "Participate in deployment and release processes",
            "Collaborate closely with Product Owners, Architects, QA and frontend teams",
            "Support onboarding of new team members",
            "Participate in client meetings to refine requirements",
            "Client project running on OpenShift — hands-on Kubernetes-based deployment and orchestration",
        ),
        "Java · GraphQL · JUnit · Mockito · Jenkins · OpenShift",
    ),
    CvRole(
        "Junior Software Developer", "10.2021 — 12.2022", "GFT Poland Sp. z o. o. · Warsaw",
        listOf(
            "Designed and implemented microservices in Kotlin for Kubernetes",
            "Built RESTful APIs and maintained technical documentation",
            "Supported application deployment processes",
            "Collaborated with Product Owners, QA and DevOps teams",
            "Participated in requirements gathering and technical discussions with clients",
        ),
        "Kotlin coroutines · MockWebServer · Docker · Kubernetes",
        soft = true,
    ),
)

private data class CvProject(val name: String, val description: String, val stack: String)

private val ownProjects = listOf(
    CvProject(
        "SW Wiki",
        "Mobile app fetching Star Wars data from external APIs, including image search by keywords. On Google Play.",
        "Apollo · Jetpack Compose · Gradle · Kotlin",
    ),
    CvProject(
        "DatabaseSchedulerExecutor",
        "Kotlin app that executes SQLite queries at scheduled times based on CRON expressions, configurable via a properties file.",
        "Kotlin · JDBC SQLite · CRON",
    ),
)

private val skillGroups = listOf(
    "BACKEND" to "Kotlin, Java, Spring, GraphQL, Coroutines",
    "TESTING" to "JUnit, Mockito, MockWebServer, Cucumber",
    "DEVOPS / CLOUD" to "Docker, Kubernetes, OpenShift, Jenkins",
    "OTHER" to "RESTful API design, microservices architecture",
)

private val navLinkVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)

private const val Description = "Adrian Jagieło's CV — Kotlin and JVM backend experience, Kubernetes, OpenShift, REST and GraphQL APIs."

@InitRoute
fun initCvPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("CV", Description))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun CvPage() {
    Section(attrs = { classes("band", "band--strong", "cv-head") }) {
        Div {
            Div(attrs = { classes("kicker"); style { property("margin-bottom", "18px") } }) { Text("CURRICULUM VITAE") }
            H1(attrs = { classes("display"); style { property("font-size", "clamp(38px, 5.5vw, 80px)") } }) {
                Text("ADRIAN JAGIEŁO")
                Br()
                Em(attrs = { classes("red") }) { Text("KOTLIN | JAVA DEVELOPER") }
            }
        }
        Div(attrs = { classes("cv-contact") }) {
            Link("mailto:jagielo.adrian@gmail.com", "jagielo.adrian@gmail.com", Modifier, variant = navLinkVariant)
            Link("https://www.linkedin.com/in/jagieloadrian/", "linkedin.com/in/jagieloadrian ↗", Modifier, variant = navLinkVariant)
            Link("https://github.com/jagieloadrian", "github.com/jagieloadrian ↗", Modifier, variant = navLinkVariant)
            Span { Text("Warsaw, Poland") }
        }
    }

    Section(attrs = { classes("band", "band--strong", "split") }) {
        Div {
            Div(attrs = { classes("label", "label--sm"); style { property("margin-bottom", "20px") } }) { Text("EXPERIENCE") }
            experience.forEach { role ->
                Div(attrs = { classes(buildList { add("tl"); if (role.soft) add("tl--soft") }); style { property("margin-bottom", "32px") } }) {
                    Div(attrs = { classes("tags"); style { property("align-items", "baseline"); property("gap", "12px") } }) {
                        Span(attrs = { style { property("font-size", "22px"); property("font-weight", "800") } }) { Text(role.title) }
                        Span(attrs = { classes("tl-when") }) { Text(role.date) }
                    }
                    Div(attrs = { classes("tl-where") }) { Text(role.where) }
                    Ul(attrs = { classes("bullets") }) {
                        role.bullets.forEach { bullet -> Li { Text(bullet) } }
                    }
                    Div(attrs = { classes("tl-stack") }) { Text(role.stack) }
                }
            }
            Div(attrs = { classes("rule", "rule--soft"); style { property("margin", "36px 0 28px") } })
            Div(attrs = { classes("label", "label--sm"); style { property("margin-bottom", "18px") } }) { Text("OWN PROJECTS") }
            ownProjects.forEach { project ->
                Div(attrs = { style { property("margin-bottom", "22px") } }) {
                    Div(attrs = { style { property("font-size", "19px"); property("font-weight", "800") } }) { Text(project.name) }
                    P(attrs = { classes("body", "body--sm"); style { property("max-width", "70ch"); property("margin", "8px 0 0") } }) {
                        Text(project.description)
                    }
                    Div(attrs = { classes("tl-stack") }) { Text(project.stack) }
                }
            }
            Link("/projects", "all projects →", Modifier.classNames("btn", "btn--link"), variant = navLinkVariant)
        }
        Div {
            Div(attrs = { classes("label", "label--sm"); style { property("margin-bottom", "18px") } }) { Text("SKILLS") }
            skillGroups.forEach { (label, text) ->
                Div(attrs = { style { property("margin-bottom", "20px") } }) {
                    Div(attrs = { classes("label", "label--sm", "label--red"); style { property("letter-spacing", "0.16em"); property("margin-bottom", "8px") } }) {
                        Text(label)
                    }
                    Div(attrs = { style { property("font-size", "15px"); property("line-height", "1.7"); property("color", "var(--ink-2)") } }) { Text(text) }
                }
            }
            Div(attrs = { classes("rule", "rule--soft"); style { property("margin-bottom", "24px") } })
            Div(attrs = { classes("label", "label--sm"); style { property("margin-bottom", "12px") } }) { Text("LANGUAGES") }
            Div(attrs = { style { property("font-size", "15px"); property("line-height", "1.8"); property("color", "var(--ink-2)") } }) {
                Text("Polish — native"); Br(); Text("English — professional")
            }
            Div(attrs = { classes("rule", "rule--soft"); style { property("margin", "24px 0") } })
            Div(attrs = { classes("label", "label--sm"); style { property("margin-bottom", "12px") } }) { Text("INTERESTS") }
            Div(attrs = { style { property("font-size", "15px"); property("line-height", "1.8"); property("color", "var(--ink-2)") } }) {
                Text("History of video games"); Br(); Text("Motorcycles"); Br(); Text("Cooking")
            }
        }
    }
}
