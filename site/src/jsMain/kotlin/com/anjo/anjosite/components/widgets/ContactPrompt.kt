package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.browser.uri.encodeURIComponent
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

// Literal port of docs/handoff/index.html's <div class="term"> contact prompt (data-chat +
// data-msg + data-send in the mock) and app.js's sendMessage(): types a message, appends the
// exchange to the log, then hands off to the visitor's mail client via mailto: — no backend.
private data class ChatLine(val text: String, val styleClass: String)

@Composable
fun ContactPrompt(recipientEmail: String, subject: String) {
    var message by remember { mutableStateOf("") }
    val log = remember { mutableStateListOf<ChatLine>() }

    fun send() {
        val trimmed = message.trim()
        if (trimmed.isEmpty()) return
        log.add(ChatLine("> $trimmed", "t-out"))
        log.add(ChatLine("opening your mail client — mailto:$recipientEmail", "t-pink"))
        message = ""
        window.location.href = "mailto:$recipientEmail" +
            "?subject=${encodeURIComponent(subject)}" +
            "&body=${encodeURIComponent(trimmed)}"
    }

    Div(attrs = { classes("term") }) {
        Div(attrs = { classes("term-bar") }) {
            Span { Text("adrian@d18: ~/contact") }
            Span { Text("msg") }
        }
        Div(attrs = { classes("term-body") }) {
            P(attrs = { classes("t-cmd") }) { Text("$ ./message --to adrian") }
            log.forEach { line -> P(attrs = { classes(line.styleClass) }) { Text(line.text) } }
        }
        Div(attrs = { classes("term-input") }) {
            Span { Text(">") }
            Input(InputType.Text, attrs = {
                value(message)
                placeholder("type your message, hit enter")
                onInput { message = it.value }
                onKeyDown { if (it.key == "Enter") send() }
            })
            Button(attrs = {
                classes("btn", "btn--sm", "btn--outline")
                onClick { send() }
            }) { Text("send") }
        }
    }
}
