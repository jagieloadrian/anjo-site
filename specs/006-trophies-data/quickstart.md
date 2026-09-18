# Quickstart: PSN Trophies Data Automation

Manual validation guide (User Story 3). No automated test suite in this project, matching every
prior phase (constitution: no testing framework mandated).

## Prerequisites

- Node.js 20+ installed locally.
- Your own `NPSSO` token: log into <https://www.playstation.com/>, then in the same browser visit
  <https://ca.account.sony.com/api/v1/ssocookie> and copy the `npsso` value from the JSON response.
  Treat it like a password — never commit it, never paste it into a shared chat.

## 1. Install the script's dependency

```bash
cd scripts/refresh-trophies
npm install
```

## 2. Run it locally against your real PSN account

```bash
NPSSO=<paste your token> node index.mjs
```

**Expected outcome**: exits 0, and
`git diff --stat -- site/src/jsMain/resources/public/trophies.json` (run from the repo root) shows
a changed file — real stats/games/trophies, no `—` placeholders.

## 3. Verify the output shape

```bash
cat ../../site/src/jsMain/resources/public/trophies.json | python3 -m json.tool
```

Confirm:

- `stats` has exactly 8 entries with keys `level`, `games`, `completion`, `platinums`, `total`,
  `gold`, `silver`, `bronze` — none missing, none empty-string.
- `games` has up to 6 entries, each `tier`-free (it's a game, not a trophy) with a real
  `imageUrl`.
- `trophies` has up to 10 entries, each `tier` one of exactly `BRONZE`/`SILVER`/`GOLD`/`PLATINUM`
  (matches `TrophyTier.valueOf` in `Trophies.kt` — any other casing crashes the page).

## 4. Confirm the site renders it (no crash)

```bash
cd ../..
./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC
```

Open `site/.kobweb/site/trophies/index.html` in a browser (or serve the export dir) and confirm the
Trophies page shows real data with no console error.

## 5. Verify the failure path (User Story 2)

```bash
cd scripts/refresh-trophies
NPSSO=not-a-real-token node index.mjs; echo "exit code: $?"
```

**Expected outcome**: non-zero exit code, and `trophies.json` is byte-for-byte unchanged
(`git status` shows no diff on that file).

## 6. Nightly workflow (CI)

Trigger manually once to confirm the wiring: **Actions → Refresh trophies → Run workflow** on
GitHub. Expected: green run, a new commit `chore: refresh trophies.json` on `main` if your data
changed since the last run (or no commit at all if nothing changed — not a failure).
