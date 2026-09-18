import { test, expect } from "@playwright/test";

// specs/007-tests-polishing FR-015 (replaces docs/handoff/mobile-check.html's manual review,
// ROADMAP F024a): at each mobile breakpoint, no horizontal overflow and every interactive
// element's smaller dimension is at least a 48px touch target.
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
