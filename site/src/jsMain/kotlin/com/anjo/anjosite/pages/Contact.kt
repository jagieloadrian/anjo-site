package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.ContactPrompt
import com.anjo.anjosite.components.widgets.LinkCell
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text

private const val RecipientEmail = "jagielo.adrian@gmail.com"
private const val Subject = "Hello from the site"

private const val Description = "Get in touch with Adrian Jagieło — email, LinkedIn, GitHub, and more."

@InitRoute
fun initContactPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Contact", Description))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun ContactPage() {
    Section(attrs = { classes("band", "band--strong", "band--pad") }) {
        Div(attrs = { classes("kicker"); style { property("margin-bottom", "18px") } }) { Text("05 / CONTACT") }
        H1(attrs = { classes("display") }) {
            Text("SAY")
            Br()
            Em { Text("SOMETHING") }
        }
    }
    Section(attrs = { classes("band", "band--strong", "split") }) {
        Div {
            Div(attrs = { classes("label", "label--sm"); style { property("margin-bottom", "18px") } }) { Text("CHANNELS") }
            Div(attrs = { classes("links", "links--stack") }) {
                LinkCell("mailto:$RecipientEmail", "EMAIL", RecipientEmail)
                LinkCell("https://www.linkedin.com/in/jagieloadrian/", "LINKEDIN", "/in/jagieloadrian ↗")
                LinkCell("https://github.com/jagieloadrian", "GITHUB", "jagieloadrian ↗")
                LinkCell("https://play.google.com/store/apps/developer?id=diether18", "PLAY STORE", "diether18 ↗")
                LinkCell("https://psnprofiles.com/Sirdiether18", "PSN", "Sirdiether18 ↗")
            }
        }
        Div {
            Div(attrs = { classes("label", "label--sm"); style { property("margin-bottom", "18px") } }) { Text("CONTACT PROMPT") }
            ContactPrompt(RecipientEmail, Subject)
            P(attrs = { classes("meta"); style { property("margin", "14px 0 0") } }) {
                Text("the prompt composes a mailto: link — no backend, nothing stored")
            }
        }
    }
}
