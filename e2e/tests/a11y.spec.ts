import { test, expect } from "@playwright/test";
import AxeBuilder from "@axe-core/playwright";

// specs/007-tests-polishing FR-003/FR-005/FR-017: every route, in both color modes, scans clean
// against axe-core's wcag2a+wcag2aa rule tags.
const routes = ["/", "/about", "/projects", "/trophies", "/contact", "/cv", "/404", "/projects/star-wars-wiki-compose"];
const themes: Array<"light" | "dark"> = ["dark", "light"];

for (const route of routes) {
  for (const theme of themes) {
    test(`${route} has no wcag2a/wcag2aa violations (${theme})`, async ({ page }) => {
      // Theme.kt's detectInitialTheme() reads localStorage's "aj-theme" first, falling back to
      // prefers-color-scheme only when unset — pre-seeding it is the only way to pin the theme
      // deterministically; the OS-preference fallback made Chromium's own default (light) leak
      // into an unrelated test here before this fix.
      await page.addInitScript((t) => localStorage.setItem("aj-theme", t), theme);
      await page.goto(route);
      await expect(page.locator("html")).toHaveAttribute("data-theme", theme);

      const results = await new AxeBuilder({ page }).withTags(["wcag2a", "wcag2aa"]).analyze();
      expect(results.violations).toEqual([]);
    });
  }
}
