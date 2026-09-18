package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.Fact
import com.anjo.anjosite.components.widgets.LinkCell
import com.anjo.anjosite.components.widgets.StatCell
import com.anjo.anjosite.components.widgets.StatColor
import com.anjo.anjosite.components.widgets.StatItem
import com.anjo.anjosite.components.widgets.Tag
import com.anjo.anjosite.components.widgets.TagColor
import com.anjo.anjosite.components.widgets.Terminal
import com.anjo.anjosite.components.widgets.TerminalLine
import com.anjo.anjosite.components.widgets.TerminalLineStyle
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
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val HeroLede = "Software developer specializing in Kotlin and JVM backend development. I mentor team members, turn complex technical topics into material other developers can actually use, and build things for fun on the side."
private const val AboutTeaserLead = "I design and maintain backend services in Kotlin and Java — microservices on Kubernetes and OpenShift, REST and GraphQL APIs, and the documentation and release processes around them. I review code, onboard new developers, and sit in client meetings where requirements get shaped."
private const val AboutTeaserOutside = "Outside work: the history of video games, motorcycles, cooking."

private val bootLines: List<TerminalLine> = listOf(
    TerminalLine("$ whoami", TerminalLineStyle.COMMAND),
    TerminalLine("adrian.jagielo :: software developer", TerminalLineStyle.OUTPUT),
    TerminalLine("$ cat stack.kt", TerminalLineStyle.COMMAND),
    TerminalLine("""val core = listOf("Kotlin", "Java", "Spring Boot")""", TerminalLineStyle.ACCENT_PINK),
    TerminalLine("$ systemctl status career", TerminalLineStyle.COMMAND),
    TerminalLine("● gft-poland.service — active (running) since 10.2021", TerminalLineStyle.OUTPUT),
    TerminalLine("$ echo \$INTERESTS", TerminalLineStyle.COMMAND),
    TerminalLine("video games / motorcycles / cooking", TerminalLineStyle.ACCENT_CYAN),
    TerminalLine("$ ./open --projects", TerminalLineStyle.COMMAND),
)

private data class StackGroup(val label: String, val labelColor: TagColor, val tags: List<Pair<String, TagColor>>)

private sealed interface StackFetchState {
    data object Loading : StackFetchState
    data class Loaded(val groups: List<StackGroup>) : StackFetchState
    data object Failed : StackFetchState
}

private fun parseStackGroups(text: String): List<StackGroup> {
    val json = JSON.parse<dynamic>(text)
    return (json.groups as Array<dynamic>).map { group ->
        StackGroup(
            label = group.label as String,
            labelColor = TagColor.valueOf(group.labelColor as String),
            tags = (group.tags as Array<dynamic>).map { it.text as String to TagColor.valueOf(it.color as String) },
        )
    }
}

private const val StackLoadingLabel = "Loading stack…"
private const val StackErrorLabel = "Stack couldn't be loaded right now."

private val navLinkVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)

private const val Description = "Adrian Jagieło — software developer specializing in Kotlin and JVM backend development."

@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Home", Description))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun HomePage() {
    var stackState by remember { mutableStateOf<StackFetchState>(StackFetchState.Loading) }
    LaunchedEffect(Unit) {
        stackState = try {
            val response = window.fetch("/stack.json").await()
            if (!response.ok) throw Exception("HTTP ${response.status}")
            StackFetchState.Loaded(parseStackGroups(response.text().await()))
        } catch (t: Throwable) {
            console.log(t)
            StackFetchState.Failed
        }
    }

    Section(attrs = { classes("band", "band--strong", "split") }) {
        Div {
            Div(attrs = { classes("kicker") }) { Text("// KOTLIN | JAVA DEVELOPER") }
            H1(attrs = { classes("display", "display--xl"); style { property("margin-top", "20px") } }) {
                Span(attrs = { classes("glitch"); attr("data-text", "ADRIAN") }) { Text("ADRIAN") }
                Br()
                Em(attrs = { classes("glow") }) { Text("JAGIEŁO") }
            }
            Div(attrs = { classes("rule") })
            P(attrs = { classes("body"); style { property("max-width", "46ch"); property("font-size", "17px"); property("color", "var(--dim)") } }) {
                Text(HeroLede)
            }
            Div(attrs = { classes("tags"); style { property("gap", "12px"); property("margin-top", "32px") } }) {
                Link("/projects", "view projects →", Modifier.classNames("btn"), variant = navLinkVariant)
                Link("/cv", "read cv", Modifier.classNames("btn", "btn--ghost"), variant = navLinkVariant)
            }
        }
        Div {
            Terminal(bootLines)
            Div(attrs = { classes("facts"); style { property("margin-top", "24px") } }) {
                Fact(label = "SINCE", value = "10.2021")
                Fact(label = "EMPLOYER", value = "GFT")
                Fact(label = "BASE", value = "WARSAW")
            }
        }
    }

    Section(attrs = { classes("band", "labelled") }) {
        Div {
            H2(attrs = { classes("label") }) { Text("02 / About") }
            Link("/about", "full page →", Modifier.classNames("btn", "btn--link"), variant = navLinkVariant)
        }
        Div {
            P(attrs = { classes("lead"); style { property("font-size", "18px") } }) { Text(AboutTeaserLead) }
            P(attrs = { classes("body", "body--sm"); style { property("color", "var(--mut)"); property("margin", "0") } }) {
                Text(AboutTeaserOutside)
            }
        }
    }

    Section(attrs = { classes("band", "labelled") }) {
        Div {
            H2(attrs = { classes("label") }) { Text("Stack") }
            P(attrs = { classes("meta"); style { property("margin", "14px 0 0") } }) {
                Text("source: skills.json")
                Br()
                Text("repos + own projects")
            }
        }
        when (val state = stackState) {
            is StackFetchState.Loading -> P(attrs = { classes("body") }) { Text(StackLoadingLabel) }
            is StackFetchState.Failed -> P(attrs = { classes("body") }) { Text(StackErrorLabel) }
            is StackFetchState.Loaded -> {
                Div(attrs = { classes("stack-groups") }) {
                    state.groups.forEach { group ->
                        Div {
                            Div(attrs = {
                                classes(buildList {
                                    add("label"); add("label--sm")
                                    if (group.labelColor == TagColor.CYAN) add("label--cyan")
                                    if (group.labelColor == TagColor.RED) add("label--red")
                                })
                                style { property("margin-bottom", "12px") }
                            }) { Text(group.label) }
                            Div(attrs = { classes("tags") }) {
                                group.tags.forEach { (text, color) -> Tag(text, color) }
                            }
                        }
                    }
                }
            }
        }
    }

    Section(attrs = { classes("band", "band--strong", "labelled") }) {
        Div {
            H2(attrs = { classes("label") }) { Text("04 / Trophies") }
            Link("/trophies", "trophy cabinet →", Modifier.classNames("btn", "btn--link"), variant = navLinkVariant)
        }
        Div {
            Div(attrs = { classes("stats") }) {
                StatCell(StatItem("PSN LEVEL", "—"))
                StatCell(StatItem("PLATINUMS", "—"))
                StatCell(StatItem("GAMES", "—", StatColor.CYAN))
                StatCell(StatItem("COMPLETION", "—", StatColor.RED))
            }
            P(attrs = { classes("meta"); style { property("margin", "16px 0 0") } }) {
                Text("values load from trophies.json — placeholders until the pipeline is wired")
            }
        }
    }

    Section(attrs = { classes("labelled") }) {
        Div {
            H2(attrs = { classes("label") }) { Text("05 / Contact") }
        }
        Div {
            Div(attrs = { classes("links") }) {
                LinkCell("mailto:jagielo.adrian@gmail.com", "EMAIL", "jagielo.adrian@gmail.com")
                LinkCell("https://www.linkedin.com/in/jagieloadrian/", "LINKEDIN", "/in/jagieloadrian ↗")
                LinkCell("https://github.com/jagieloadrian", "GITHUB", "jagieloadrian ↗")
                LinkCell("https://psnprofiles.com/Sirdiether18", "PSN", "Sirdiether18 ↗")
            }
        }
    }
}
