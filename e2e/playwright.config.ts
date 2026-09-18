import { defineConfig, devices } from "@playwright/test";

// specs/007-tests-polishing: runs against a real `kobwebExport --layout static` output (never
// the dev server — Constitution Principle VII), served locally by python3's stdlib http.server
// (research.md §5: zero new dependency, always present on ubuntu-latest).
const PORT = 4173;
const BASE_URL = `http://127.0.0.1:${PORT}`;

export default defineConfig({
  testDir: "./tests",
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  reporter: "list",
  use: {
    baseURL: BASE_URL,
    trace: "on-first-retry",
  },
  // Chromium only (spec Assumption): the reasonable minimum for a personal portfolio site with
  // no reported cross-browser bugs.
  projects: [
    {
      name: "chromium",
      use: { ...devices["Desktop Chrome"] },
    },
  ],
  webServer: {
    // serve-static.py (research.md §5): plain `python3 -m http.server` has no SPA/GitHub-Pages
    // fallback, so a client-side route like /projects/{unknown-slug} (routes.spec.ts, FR-012)
    // would 404 at the HTTP layer instead of booting the app.
    command: `python3 serve-static.py ${PORT} ../site/.kobweb/site`,
    url: BASE_URL,
    reuseExistingServer: !process.env.CI,
    timeout: 30_000,
  },
});
