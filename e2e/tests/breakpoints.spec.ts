import { test, expect } from "@playwright/test";

const breakpoints = [390, 430, 768];
const routes = ["/", "/about", "/projects", "/trophies", "/contact", "/cv"];

for (const width of breakpoints) {
  for (const route of routes) {
    test(`${route} at ${width}px has no horizontal overflow`, async ({ page }) => {
      await page.setViewportSize({ width, height: 800 });
      await page.goto(route);

      const { scrollWidth, clientWidth } = await page.evaluate(() => ({
        scrollWidth: document.documentElement.scrollWidth,
        clientWidth: document.documentElement.clientWidth,
      }));
      expect(scrollWidth).toBeLessThanOrEqual(clientWidth);
    });
  }
}

test("nav links and buttons meet the 48px touch target at 390px", async ({ page }) => {
  await page.setViewportSize({ width: 390, height: 800 });
  await page.goto("/");

  const sizes = await page.locator(".nav-btn, .lang-btn, .theme-btn").evaluateAll((els) =>
    els.map((el) => {
      const r = el.getBoundingClientRect();
      return Math.min(r.width, r.height);
    })
  );

  for (const size of sizes) {
    expect(size).toBeGreaterThanOrEqual(48);
  }
});
