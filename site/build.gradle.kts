import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link

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
                link(rel = "preconnect", href = "https://fonts.googleapis.com")
                link(rel = "preconnect", href = "https://fonts.gstatic.com") {
                    attributes["crossorigin"] = ""
                }
                link(
                    rel = "stylesheet",
                    href = "https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800;900&family=JetBrains+Mono:wght@400;500;700&display=swap"
                )
                // The mock's own hand-authored stylesheet (docs/handoff/styles.css), served
                // verbatim (plus a print block it doesn't have) — pages are built with its exact
                // class names so the site actually matches the mock instead of a Kotlin
                // approximation of it (see PROJECT-STATUS.md for why this replaced the earlier
                // Silk-CssStyle-only approach).
                link(rel = "stylesheet", href = "/styles.css")
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

        // Uncomment the following if you pass `includeServer = true` into the `configAsKobwebApplication` call.
//        jvmMain.dependencies {
//            compileOnly(libs.kobweb.api) // Provided by Kobweb backend at runtime
//        }
    }
}
