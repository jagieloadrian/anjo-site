import { test, expect } from "@playwright/test";

const staticRoutes = ["/", "/about", "/projects", "/trophies", "/contact", "/cv", "/404"];

// Filters Kobweb dev-server hot-reload noise, not app regressions.
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
