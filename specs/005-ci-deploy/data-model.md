# Data Model: CI/Deploy

No application data model — this feature adds CI configuration, not app state. The "entities"
below are the workflow/config shapes that `research.md` and `plan.md` reference; they exist as
YAML files and repo/environment secrets, not as Kotlin types or persisted records.

## Deploy Workflow (`.github/workflows/deploy.yml`)

| Field | Value | Notes |
|---|---|---|
| Trigger | `push` to `main`, plus `workflow_dispatch` | manual re-run without an empty commit |
| Concurrency group | `deploy`, `cancel-in-progress: true` | research.md §5 |
| Build step | `./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC` | research.md §1 |
| JDK | Temurin 21 via `actions/setup-java@v4` | research.md §2 |
| Publish artifact source | `site/.kobweb/site/` | matches `.kobweb/conf.yaml`'s `server.files.prod.siteRoot` |
| Publish destination | `jagieloadrian/jagieloadrian.github.io`, branch `main` | research.md §4 |
| Publish mechanism | `peaceiris/actions-gh-pages@v4`, `deploy_key` auth | research.md §4 |

## Trophy Refresh Workflow (`.github/workflows/trophies.yml`)

| Field | Value | Notes |
|---|---|---|
| Trigger | `schedule` (nightly cron) + `workflow_dispatch` | research.md §7 |
| Credential | `NPSSO` repo secret → env var, never a log/arg | constitution Principle VI |
| Data step | placeholder until ROADMAP F025 ships a real `psn-api` script | research.md §7 |
| Output | `site/src/jsMain/resources/public/trophies.json`, committed back to `main` if changed | research.md §7 |

## Publish Destination

The root-domain address the site is actually served from, distinct from the repository that
builds it (spec.md Key Entities).

| Field | Value |
|---|---|
| Repo | `jagieloadrian/jagieloadrian.github.io` |
| Branch | `main` (root) |
| Live URL | `https://jagieloadrian.github.io/` |
| Written by | Deploy Workflow's publish step (research.md §4) |

## Repo Secrets (both live only in GitHub Actions secret storage — never in code, logs, or the published bundle)

| Secret | Used by | Purpose |
|---|---|---|
| Deploy key (SSH private key) | Deploy Workflow | write access to `jagieloadrian.github.io` only |
| `NPSSO` | Trophy Refresh Workflow | PlayStation session credential (ROADMAP F025 scope) |

## One-time manual prerequisite (not part of either workflow's own state)

- GitHub Pages enabled on `jagieloadrian.github.io`, source = `main` branch root — research.md §8.
