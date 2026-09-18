Phase 5 — CI/Deploy: automate what has so far only been run by hand — `kobweb export
-PkobwebExportLayout=STATIC` from `site/` — as a GitHub Actions workflow that builds and publishes
the static output on every push to `main`, plus a separate nightly workflow for `trophies.json`.
No `.github/workflows/` directory exists yet in this repo.

Scope (maps to ROADMAP.md F027–F029):

1. Build + deploy workflow (F027)
   Create `.github/workflows/deploy.yml` (or similar): on push to `main` (and optionally manual
   `workflow_dispatch`), set up JDK (Kobweb/Gradle need a JVM toolchain — check
   `site/build.gradle.kts` / `gradle.properties` for the exact version), run
   `./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC` (or the CLI-equivalent
   `kobweb export -PkobwebExportLayout=STATIC` if the workflow installs the Kobweb CLI instead of
   using the Gradle plugin task directly — confirm which of these two invocations this project
   actually uses, since `specs/004-pages/quickstart.md` documents the CLI form but a CI runner may
   prefer the Gradle task form for reproducibility/caching), then publish the exported output
   (`site/.kobweb/site` per `.kobweb/conf.yaml`'s `server.files.prod.siteRoot`) to GitHub Pages.
   Decide the actual publish mechanism: GitHub's native `actions/deploy-pages` (Pages configured
   to deploy from a workflow artifact) vs. pushing the exported output to a `gh-pages` branch
   (ROADMAP's own wording: "deploy `gh-pages`") vs., if F028 confirms the user-page decision below,
   pushing to the separate `jagieloadrian.github.io` repository instead of a branch in this repo —
   pick one and document why, since GitHub Pages' source setting and the workflow's publish step
   must agree.

2. Project page vs. user page — confirm, don't re-decide (F028)
   This was already resolved in Phase 0 (`specs/001-foundation-setup/spec.md` Clarifications,
   `research.md` §1): a **user/organization root page** (`jagieloadrian.github.io`), `basePath`
   left unset (`""` = root) in `.kobweb/conf.yaml`. Phase 0's Assumptions explicitly deferred "the
   exact publish mechanism, e.g. a cross-repo CI push" to planning — that's this phase's job:
   figure out how a workflow running in the `anjo-site` repo actually gets its static export
   published to the `jagieloadrian.github.io` repository (a deploy key / PAT pushing to that
   second repo, or a different structure entirely). If anything changes about the root-page
   decision itself, it must also update `site/build.gradle.kts`'s `configAsKobwebApplication` /
   `.kobweb/conf.yaml`'s `basePath` to match — but the default expectation is confirmation, not a
   reversal.

3. Nightly `trophies.json` workflow (F029)
   Separate from the deploy workflow: a scheduled (`on: schedule`, cron) GitHub Action that
   regenerates `site/src/jsMain/resources/public/trophies.json` from the PSN API (`psn-api` per
   ROADMAP F025 — Phase 4 scope, may or may not be done yet; check before starting this) using an
   `NPSSO` value stored as a GitHub Actions repo secret, never committed or exposed client-side
   (constitution Principle VI). Decide whether this workflow commits the regenerated JSON back to
   `main` (triggering the deploy workflow above) or writes it directly into the published output —
   committing back to `main` is simpler and keeps `trophies.json` reviewable in git history.

Out of scope for this phase: any change to page content, components, or routing (Phases 0–3 are
done); the PSN API integration itself (`psn-api` usage, NPSSO auth flow) if Phase 4 (F025/F026)
hasn't shipped it yet — this phase only wires the *scheduling and secret plumbing* around whatever
script Phase 4 produces, it doesn't write that script; SEO/analytics/self-hosted fonts (Phase 6).

Definition of done: a push to `main` triggers a workflow that runs a successful
`kobweb export -PkobwebExportLayout=STATIC` (or Gradle-task equivalent) and the result is live at
the real published GitHub Pages URL with no broken asset/link (closing Phase 0's FR-009/SC-005 loop
end-to-end, not just locally); a separate scheduled workflow exists that can regenerate
`trophies.json` without any secret appearing in a log or in the client bundle; both workflows are
demonstrated with at least one real (not just `workflow_dispatch`-dry-run) execution.
