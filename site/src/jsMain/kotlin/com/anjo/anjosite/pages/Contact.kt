package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.ContactPrompt

// Real Contact content (specs/004-pages FR-010/FR-011): wires the ContactPrompt component (the
// one new shared component this phase introduces) with the site owner's fixed email and a fixed
// subject line, matching docs/handoff/app.js's contact prompt exactly.

private const val RecipientEmail = "jagielo.adrian@gmail.com"
private const val Subject = "Hello from the site"

@InitRoute
fun initContactPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Contact"))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun ContactPage() {
    ContactPrompt(recipientEmail = RecipientEmail, subject = Subject)
}
