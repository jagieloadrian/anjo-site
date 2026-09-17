package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.TextInput
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.browser.uri.encodeURIComponent
import kotlinx.browser.window
import org.jetbrains.compose.web.css.cssRem
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.SitePalette
import com.anjo.anjosite.TouchTargetStyle
import com.anjo.anjosite.toSitePalette
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode

// The one new shared component this phase introduces (FR-014 exception, specs/004-pages
// clarification session): a single free-text message field + send button that builds a
// mailto: link client-side, matching docs/handoff/app.js's contact prompt exactly (single
// field, fixed subject/recipient supplied by the caller, not visitor-entered).

data class ContactMessage(val body: String)

// Internal chrome text (analyze finding B1) — not caller parameters, since only
// recipient/subject vary by call site; read via LocalLang.current, same as NavHeader.kt's own
// chrome text (e.g. LangSwitchButton).
private val SendLabel = BilingualString(en = "Send", pl = "Wyślij")
private val MessageRequiredLabel = BilingualString(en = "Message required", pl = "Wiadomość jest wymagana")
private val MessagePlaceholder = BilingualString(
    en = "type your message, hit enter",
    pl = "wpisz wiadomość, wciśnij enter",
)

@Composable
fun ContactPrompt(recipientEmail: String, subject: String) {
    val lang = LocalLang.current
    val sitePalette: SitePalette = ColorMode.current.toSitePalette()
    var message by remember { mutableStateOf(ContactMessage("")) }
    var showRequiredError by remember { mutableStateOf(false) }

    fun send() {
        if (message.body.isBlank()) {
            showRequiredError = true
            return
        }
        showRequiredError = false
        window.location.href = "mailto:$recipientEmail" +
            "?subject=${encodeURIComponent(subject)}" +
            "&body=${encodeURIComponent(message.body)}"
    }

    Column(Modifier.gap(0.75.cssRem)) {
        Row(Modifier.gap(0.75.cssRem)) {
            TextInput(
                text = message.body,
                onTextChange = {
                    message = ContactMessage(it)
                    showRequiredError = false
                },
                placeholder = MessagePlaceholder(lang),
                modifier = TouchTargetStyle.toModifier(),
            )
            Button(onClick = { send() }, modifier = TouchTargetStyle.toModifier()) {
                SpanText(SendLabel(lang))
            }
        }
        if (showRequiredError) {
            SpanText(MessageRequiredLabel(lang), Modifier.fontSize(0.8125.cssRem).color(sitePalette.red))
        }
    }
}
