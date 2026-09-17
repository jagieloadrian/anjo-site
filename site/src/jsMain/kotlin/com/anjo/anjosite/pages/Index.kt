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
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.vw
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.Lang
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.TouchTargetStyle
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.Terminal
import com.anjo.anjosite.components.widgets.TerminalLine
import com.anjo.anjosite.components.widgets.TerminalLineStyle
import com.varabyte.kobweb.silk.style.toModifier

// Real Home content ported from docs/handoff/index.html's HOME screen + app.js's BOOT.en/pl
// (specs/004-pages FR-001/FR-002), replacing the Phase 0/1 token-swatch + language-demo
// placeholder.

private val HeroKicker = BilingualString(en = "// KOTLIN | JAVA DEVELOPER", pl = "// KOTLIN | JAVA DEVELOPER")
private val HeroLede = BilingualString(
    en = "Software developer specializing in Kotlin and JVM backend development. I mentor team members, turn complex technical topics into material other developers can actually use, and build things for fun on the side.",
    pl = "Software developer specjalizujący się w Kotlinie i backendzie na JVM. Mentoruję zespół, tłumaczę złożone tematy techniczne na materiał przydatny innym programistom i buduję własne projekty dla przyjemności.",
)
private val ViewProjectsLabel = BilingualString(en = "view projects →", pl = "zobacz projekty →")
private val ReadCvLabel = BilingualString(en = "read cv", pl = "zobacz cv")

private fun bootLines(lang: Lang): List<TerminalLine> = when (lang) {
    Lang.EN -> listOf(
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
    Lang.PL -> listOf(
        TerminalLine("$ whoami", TerminalLineStyle.COMMAND),
        TerminalLine("adrian.jagielo :: software developer", TerminalLineStyle.OUTPUT),
        TerminalLine("$ cat stack.kt", TerminalLineStyle.COMMAND),
        TerminalLine("""val core = listOf("Kotlin", "Java", "Spring Boot")""", TerminalLineStyle.ACCENT_PINK),
        TerminalLine("$ systemctl status kariera", TerminalLineStyle.COMMAND),
        TerminalLine("● gft-poland.service — aktywny (działa) od 10.2021", TerminalLineStyle.OUTPUT),
        TerminalLine("$ echo \$ZAINTERESOWANIA", TerminalLineStyle.COMMAND),
        TerminalLine("gry / motocykle / gotowanie", TerminalLineStyle.ACCENT_CYAN),
        TerminalLine("$ ./open --projekty", TerminalLineStyle.COMMAND),
    )
}

@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Home"))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun HomePage() {
    val lang = LocalLang.current

    Column(Modifier.gap(2.cssRem)) {
        SpanText(
            HeroKicker(lang),
            Modifier.fontFamily("JetBrains Mono", "monospace").fontSize(0.875.cssRem),
        )
        SpanText(
            "ADRIAN JAGIEŁO",
            Modifier
                .fontFamily("Archivo", "system-ui", "sans-serif")
                .fontWeight(900)
                .fontSize(clamp(2.75.cssRem, 6.5.vw, 6.5.cssRem)),
        )
        SpanText(HeroLede(lang), Modifier.fontSize(1.0625.cssRem).maxWidth(46.cssRem))
        Row(Modifier.gap(0.75.cssRem)) {
            Link("/projects", ViewProjectsLabel(lang), modifier = TouchTargetStyle.toModifier())
            Link("/cv", ReadCvLabel(lang), modifier = TouchTargetStyle.toModifier())
        }
        Terminal(bootLines(lang))
    }
}
