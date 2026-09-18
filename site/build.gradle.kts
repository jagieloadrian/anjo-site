import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link
import kotlinx.html.style
import kotlinx.html.unsafe

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
}

group = "com.anjo.anjosite"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("Powered by Kobweb")
            // docs/favicon/README.md: browsers use the first matching `rel="icon"` link, so the
            // SVG variants (which switch on prefers-color-scheme) must come before the .ico
            // fallback. Kobweb's own `faviconPath` convenience always emits its link before our
            // head.add block, which would put the .ico first — disabled here so we control order.
            faviconPath.set("")
            // Archivo + JetBrains Mono, matching docs/handoff/index.html (research.md §2).
            head.add {
                link(rel = "icon", href = "/favicon.svg") {
                    type = "image/svg+xml"
                    media = "(prefers-color-scheme: dark)"
                }
                link(rel = "icon", href = "/favicon-light.svg") {
                    type = "image/svg+xml"
                    media = "(prefers-color-scheme: light)"
                }
                link(rel = "icon", href = "/favicon-32.png") {
                    type = "image/png"
                    sizes = "32x32"
                }
                link(rel = "shortcut icon", href = "/favicon.ico")
                link(rel = "apple-touch-icon", href = "/apple-touch-icon.png") {
                    sizes = "180x180"
                }
                link(rel = "manifest", href = "/site.webmanifest")
                // Self-hosted (specs/007-tests-polishing research.md §10/FR-007): replaces the
                // Google Fonts CDN link. Vendored in resources/public/fonts/ — `latin` +
                // `latin-ext` subsets per weight (Constitution Principle III: Polish diacritics
                // live in the latin-ext Unicode block). Archivo/JetBrains Mono are variable fonts,
                // so all declared weights of a subset share one file (unicode-range differs);
                // Compose HTML's typed StyleSheet DSL has no @font-face support, hence the raw tag.
                style {
                    unsafe {
                        +"""
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 400; font-display: swap; src: url('/fonts/archivo-latin-ext.woff2') format('woff2'); unicode-range: U+0100-02BA, U+02BD-02C5, U+02C7-02CC, U+02CE-02D7, U+02DD-02FF, U+0304, U+0308, U+0329, U+1D00-1DBF, U+1E00-1E9F, U+1EF2-1EFF, U+2020, U+20A0-20AB, U+20AD-20C0, U+2113, U+2C60-2C7F, U+A720-A7FF; }
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 400; font-display: swap; src: url('/fonts/archivo-latin.woff2') format('woff2'); unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD; }
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 500; font-display: swap; src: url('/fonts/archivo-latin-ext.woff2') format('woff2'); unicode-range: U+0100-02BA, U+02BD-02C5, U+02C7-02CC, U+02CE-02D7, U+02DD-02FF, U+0304, U+0308, U+0329, U+1D00-1DBF, U+1E00-1E9F, U+1EF2-1EFF, U+2020, U+20A0-20AB, U+20AD-20C0, U+2113, U+2C60-2C7F, U+A720-A7FF; }
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 500; font-display: swap; src: url('/fonts/archivo-latin.woff2') format('woff2'); unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD; }
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 600; font-display: swap; src: url('/fonts/archivo-latin-ext.woff2') format('woff2'); unicode-range: U+0100-02BA, U+02BD-02C5, U+02C7-02CC, U+02CE-02D7, U+02DD-02FF, U+0304, U+0308, U+0329, U+1D00-1DBF, U+1E00-1E9F, U+1EF2-1EFF, U+2020, U+20A0-20AB, U+20AD-20C0, U+2113, U+2C60-2C7F, U+A720-A7FF; }
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 600; font-display: swap; src: url('/fonts/archivo-latin.woff2') format('woff2'); unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD; }
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 700; font-display: swap; src: url('/fonts/archivo-latin-ext.woff2') format('woff2'); unicode-range: U+0100-02BA, U+02BD-02C5, U+02C7-02CC, U+02CE-02D7, U+02DD-02FF, U+0304, U+0308, U+0329, U+1D00-1DBF, U+1E00-1E9F, U+1EF2-1EFF, U+2020, U+20A0-20AB, U+20AD-20C0, U+2113, U+2C60-2C7F, U+A720-A7FF; }
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 700; font-display: swap; src: url('/fonts/archivo-latin.woff2') format('woff2'); unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD; }
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 800; font-display: swap; src: url('/fonts/archivo-latin-ext.woff2') format('woff2'); unicode-range: U+0100-02BA, U+02BD-02C5, U+02C7-02CC, U+02CE-02D7, U+02DD-02FF, U+0304, U+0308, U+0329, U+1D00-1DBF, U+1E00-1E9F, U+1EF2-1EFF, U+2020, U+20A0-20AB, U+20AD-20C0, U+2113, U+2C60-2C7F, U+A720-A7FF; }
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 800; font-display: swap; src: url('/fonts/archivo-latin.woff2') format('woff2'); unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD; }
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 900; font-display: swap; src: url('/fonts/archivo-latin-ext.woff2') format('woff2'); unicode-range: U+0100-02BA, U+02BD-02C5, U+02C7-02CC, U+02CE-02D7, U+02DD-02FF, U+0304, U+0308, U+0329, U+1D00-1DBF, U+1E00-1E9F, U+1EF2-1EFF, U+2020, U+20A0-20AB, U+20AD-20C0, U+2113, U+2C60-2C7F, U+A720-A7FF; }
                        @font-face { font-family: 'Archivo'; font-style: normal; font-weight: 900; font-display: swap; src: url('/fonts/archivo-latin.woff2') format('woff2'); unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD; }
                        @font-face { font-family: 'JetBrains Mono'; font-style: normal; font-weight: 400; font-display: swap; src: url('/fonts/jetbrains-mono-latin-ext.woff2') format('woff2'); unicode-range: U+0100-02BA, U+02BD-02C5, U+02C7-02CC, U+02CE-02D7, U+02DD-02FF, U+0304, U+0308, U+0329, U+1D00-1DBF, U+1E00-1E9F, U+1EF2-1EFF, U+2020, U+20A0-20AB, U+20AD-20C0, U+2113, U+2C60-2C7F, U+A720-A7FF; }
                        @font-face { font-family: 'JetBrains Mono'; font-style: normal; font-weight: 400; font-display: swap; src: url('/fonts/jetbrains-mono-latin.woff2') format('woff2'); unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD; }
                        @font-face { font-family: 'JetBrains Mono'; font-style: normal; font-weight: 500; font-display: swap; src: url('/fonts/jetbrains-mono-latin-ext.woff2') format('woff2'); unicode-range: U+0100-02BA, U+02BD-02C5, U+02C7-02CC, U+02CE-02D7, U+02DD-02FF, U+0304, U+0308, U+0329, U+1D00-1DBF, U+1E00-1E9F, U+1EF2-1EFF, U+2020, U+20A0-20AB, U+20AD-20C0, U+2113, U+2C60-2C7F, U+A720-A7FF; }
                        @font-face { font-family: 'JetBrains Mono'; font-style: normal; font-weight: 500; font-display: swap; src: url('/fonts/jetbrains-mono-latin.woff2') format('woff2'); unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD; }
                        @font-face { font-family: 'JetBrains Mono'; font-style: normal; font-weight: 700; font-display: swap; src: url('/fonts/jetbrains-mono-latin-ext.woff2') format('woff2'); unicode-range: U+0100-02BA, U+02BD-02C5, U+02C7-02CC, U+02CE-02D7, U+02DD-02FF, U+0304, U+0308, U+0329, U+1D00-1DBF, U+1E00-1E9F, U+1EF2-1EFF, U+2020, U+20A0-20AB, U+20AD-20C0, U+2113, U+2C60-2C7F, U+A720-A7FF; }
                        @font-face { font-family: 'JetBrains Mono'; font-style: normal; font-weight: 700; font-display: swap; src: url('/fonts/jetbrains-mono-latin.woff2') format('woff2'); unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02BB-02BC, U+02C6, U+02DA, U+02DC, U+0304, U+0308, U+0329, U+2000-206F, U+20AC, U+2122, U+2191, U+2193, U+2212, U+2215, U+FEFF, U+FFFD; }
                        """.trimIndent()
                    }
                }
                // Site's own CSS is now SiteStyles.kt (mounted in AppEntry.kt via `Style()`), not
                // an external stylesheet — no `/styles.css` link needed anymore.
            }
        }
        export {
            // Dynamic routes (/projects/{slug}) are skipped by default export discovery — register
            // each real project slug explicitly, or its static page is silently never generated
            // (specs/004-pages research.md §1). Project content itself lives in
            // resources/public/projects.json (request #2) so day-to-day edits don't touch Kotlin —
            // but adding a brand-new slug still needs one line added here too, since Kobweb can't
            // discover dynamic routes from a runtime fetch at export time.
            addExtraRoute("/projects/star-wars-wiki-compose")
            addExtraRoute("/projects/filebrowser-api")
            addExtraRoute("/projects/database-scheduler-executor")
            addExtraRoute("/projects/tuya-key-extractor-kt")
        }
    }
}

kotlin {
    // This example is frontend only. However, for a fullstack app, you can uncomment the includeServer parameter
    // and the `jvmMain` source set below.
    configAsKobwebApplication("anjosite" /*, includeServer = true*/)

    sourceSets {
//        commonMain.dependencies {
//          // Add shared dependencies between JS and JVM here if building a fullstack app
//        }

        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            // This default template uses built-in SVG icons, but what's available is limited.
            // Uncomment the following if you want access to a large set of font-awesome icons:
            // implementation(libs.silk.icons.fa)
            implementation(libs.kobwebx.markdown)
        }

        // specs/007-tests-polishing: first automated test suite in this project (research.md §1)
        // — kotlin.test ships with the Kotlin Multiplatform plugin already applied, no new
        // version-catalog entry needed.
        jsTest.dependencies {
            implementation(kotlin("test"))
        }

        // Uncomment the following if you pass `includeServer = true` into the `configAsKobwebApplication` call.
//        jvmMain.dependencies {
//            compileOnly(libs.kobweb.api) // Provided by Kobweb backend at runtime
//        }
    }
}
