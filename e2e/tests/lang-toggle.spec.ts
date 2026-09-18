import { test, expect } from "@playwright/test";

// specs/007-tests-polishing FR-013: clicking the language switch changes visible text.
test("lang toggle swaps nav text between EN and PL", async ({ page }) => {
  await page.goto("/about");

  const homeLink = page.locator(".nav-btn").first();
  await expect(homeLink).toContainText("Home");

  await page.locator(".lang-btn", { hasText: "PL" }).click();
  await expect(homeLink).toContainText("Strona główna");

  await page.locator(".lang-btn", { hasText: "EN" }).click();
  await expect(homeLink).toContainText("Home");
});
