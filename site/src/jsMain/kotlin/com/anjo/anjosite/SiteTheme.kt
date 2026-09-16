package com.anjo.anjosite

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color

/**
 * The five design tokens ported from docs/handoff/styles.css `:root` (single source of truth,
 * constitution Principle IV). Both [SitePalettes.light] and [SitePalettes.dark] resolve to the
 * same values — the mock has exactly one visual theme (research.md §4).
 *
 * @property nearBackground A useful color to apply to a container that should differentiate itself from the background
 *   but just a little.
 */
class SitePalette(
    val background: Color,
    val ink: Color,
    val pink: Color,
    val cyan: Color,
    val red: Color,
    val nearBackground: Color,
    val brand: Brand,
) {
    class Brand(
        val primary: Color,
        val accent: Color,
    )
}

object SitePalettes {
    private val mock = SitePalette(
        background = Color.rgb(0x07070A),
        ink = Color.rgb(0xF2F0EF),
        pink = Color.rgb(0xFF2D95),
        cyan = Color.rgb(0x00E5FF),
        red = Color.rgb(0xEC3013),
        nearBackground = Color.rgb(0x121218),
        brand = SitePalette.Brand(
            primary = Color.rgb(0xFF2D95),
            accent = Color.rgb(0x00E5FF),
        )
    )

    val light = mock
    val dark = mock
}

fun ColorMode.toSitePalette(): SitePalette {
    return when (this) {
        ColorMode.LIGHT -> SitePalettes.light
        ColorMode.DARK -> SitePalettes.dark
    }
}

@InitSilk
fun initTheme(ctx: InitSilkContext) {
    // Both light and dark set to the same mock tokens (research.md §4) — this site has one theme.
    ctx.theme.palettes.light.background = SitePalettes.light.background
    ctx.theme.palettes.light.color = SitePalettes.light.ink
    ctx.theme.palettes.dark.background = SitePalettes.dark.background
    ctx.theme.palettes.dark.color = SitePalettes.dark.ink
}
