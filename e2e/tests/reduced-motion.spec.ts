import { test, expect } from "@playwright/test";

test.use({ reducedMotion: "reduce" });

test("caret has no animation under reduced motion", async ({ page }) => {
  await page.goto("/");
  const caret = page.locator(".caret").first();
  await expect(caret).toBeVisible();
  const animationName = await caret.evaluate((el) => getComputedStyle(el).animationName);
  expect(animationName).toBe("none");
});

test("glitch hover has no animation under reduced motion", async ({ page }) => {
  await page.goto("/");
  const brand = page.locator(".glitch").first();
  await brand.hover();
  const animationName = await brand.evaluate((el) => getComputedStyle(el).animationName);
  expect(animationName).toBe("none");
});

test("scanline overlay is hidden under reduced motion", async ({ page }) => {
  await page.goto("/");
  const scan = page.locator(".fx-scan");
  await expect(scan).toBeHidden();
});
