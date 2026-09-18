package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.browser.uri.encodeURIComponent
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private data class ChatLine(val text: String, val styleClass: String)

private val fallbackLinkVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)

private const val SendLabel = "Send"
private const val MessageRequiredLabel = "Message required"

@Composable
fun ContactPrompt(recipientEmail: String, subject: String) {
    var message by remember { mutableStateOf("") }
    val log = remember { mutableStateListOf<ChatLine>() }
    var lastMailto by remember { mutableStateOf<String?>(null) }
    var showRequired by remember { mutableStateOf(false) }

    fun send() {
        val trimmed = message.trim()
        if (trimmed.isEmpty()) {
            showRequired = true
            return
        }
        showRequired = false
        log.add(ChatLine("> $trimmed", "t-out"))
        log.add(ChatLine("opening your mail client — mailto:$recipientEmail", "t-pink"))
        message = ""
        val mailto = "mailto:$recipientEmail" +
            "?subject=${encodeURIComponent(subject)}" +
            "&body=${encodeURIComponent(trimmed)}"
        lastMailto = mailto
        window.location.href = mailto
    }

    Div(attrs = { classes("term") }) {
        Div(attrs = { classes("term-bar") }) {
            Span { Text("adrian@d18: ~/contact") }
            Span { Text("msg") }
        }
        Div(attrs = { classes("term-body") }) {
            P(attrs = { classes("t-cmd") }) { Text("$ ./message --to adrian") }
            log.forEach { line -> P(attrs = { classes(line.styleClass) }) { Text(line.text) } }
            if (showRequired) {
                P(attrs = { classes("t-pink") }) { Text(MessageRequiredLabel) }
            }
            lastMailto?.let { mailto ->
                Link(mailto, "nothing happen? click here →", Modifier.classNames("btn", "btn--link"), variant = fallbackLinkVariant)
            }
        }
        Div(attrs = { classes("term-input") }) {
            Span { Text(">") }
            Input(InputType.Text, attrs = {
                value(message)
                placeholder("type your message, hit enter")
                onInput { message = it.value; if (showRequired) showRequired = false }
                onKeyDown { if (it.key == "Enter") send() }
            })
            Button(attrs = {
                classes("btn", "btn--sm", "btn--outline")
                onClick { send() }
            }) { Text(SendLabel) }
        }
    }
}
