import { test, expect } from "@playwright/test";

// specs/007-tests-polishing FR-011/FR-012: every static route loads with no console error / page
// error, one dynamic project page renders its real content, and an unknown slug redirects to 404.
const staticRoutes = ["/", "/about", "/projects", "/trophies", "/contact", "/cv", "/404"];

// Kobweb's own runtime unconditionally ships a live-reload "status" widget in every exported
// layout (verified: anjosite.js calls `new EventSource("/api/kobweb-status", ...)` regardless of
// STATIC vs. FULLSTACK export) — that endpoint only exists behind Kobweb's own dev/prod server,
// never on a static host (GitHub Pages included), so it 404s and EventSource auto-reconnects
// forever. Framework runtime noise, not an application regression FR-011 is meant to catch.
const isKobwebStatusNoise = (text: string) =>
  text.includes("kobweb-status") || text.includes("EventSource");

for (const route of staticRoutes) {
  test(`${route} loads without console errors`, async ({ page }) => {
    const errors: string[] = [];
    page.on("pageerror", (err) => errors.push(err.message));
    page.on("console", (msg) => {
      if (msg.type() === "error" && !isKobwebStatusNoise(msg.text())) errors.push(msg.text());
    });

    const response = await page.goto(route);
    expect(response?.ok()).toBeTruthy();
    await page.waitForTimeout(1000);

    expect(errors).toEqual([]);
  });
}

test("dynamic project page renders its own title", async ({ page }) => {
  await page.goto("/projects/star-wars-wiki-compose");
  await expect(page.locator("h1")).toContainText("Star Wars");
  await expect(page).toHaveTitle(/Star Wars Wiki Compose/);
});

test("unknown project slug redirects to /404", async ({ page }) => {
  await page.goto("/projects/does-not-exist");
  await page.waitForURL("**/404");
  await expect(page.getByText("Page not found")).toBeVisible();
});
