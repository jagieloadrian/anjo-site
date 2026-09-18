import { test, expect } from "@playwright/test";
import AxeBuilder from "@axe-core/playwright";

const routes = ["/", "/about", "/projects", "/trophies", "/contact", "/cv", "/404", "/projects/star-wars-wiki-compose"];
const themes: Array<"light" | "dark"> = ["dark", "light"];

for (const route of routes) {
  for (const theme of themes) {
    test(`${route} has no wcag2a/wcag2aa violations (${theme})`, async ({ page }) => {
      // Pin theme before load; Chromium's default prefers-color-scheme otherwise leaks in.
      await page.addInitScript((t) => localStorage.setItem("aj-theme", t), theme);
      await page.goto(route);
      await expect(page.locator("html")).toHaveAttribute("data-theme", theme);

      const results = await new AxeBuilder({ page }).withTags(["wcag2a", "wcag2aa"]).analyze();
      expect(results.violations).toEqual([]);
    });
  }
}
