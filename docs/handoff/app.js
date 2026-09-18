/* Portfolio — vanilla behaviour layer.
   Three things only: route switching, the boot typing effect, the contact prompt.
   Port each to its Kotlin/JS equivalent (router, useEffect/LaunchedEffect, state). */

const BOOT = {
  en: [
    ["$ whoami", "t-cmd"],
    ["adrian.jagielo :: software developer", "t-out"],
    ["$ cat stack.kt", "t-cmd"],
    ['val core = listOf("Kotlin", "Java", "Spring Boot")', "t-pink"],
    ["$ systemctl status career", "t-cmd"],
    ["● gft-poland.service — active (running) since 10.2021", "t-out"],
    ["$ echo $INTERESTS", "t-cmd"],
    ["video games / motorcycles / cooking", "t-cyan"],
    ["$ ./open --projects", "t-cmd"]
  ],
  pl: [
    ["$ whoami", "t-cmd"],
    ["adrian.jagielo :: software developer", "t-out"],
    ["$ cat stack.kt", "t-cmd"],
    ['val core = listOf("Kotlin", "Java", "Spring Boot")', "t-pink"],
    ["$ systemctl status kariera", "t-cmd"],
    ["● gft-poland.service — aktywny (działa) od 10.2021", "t-out"],
    ["$ echo $ZAINTERESOWANIA", "t-cmd"],
    ["gry / motocykle / gotowanie", "t-cyan"],
    ["$ ./open --projekty", "t-cmd"]
  ]
};

const EMAIL = "jagielo.adrian@gmail.com";
const bootBox = document.querySelector("[data-boot]");
const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
let bootTimer = null;

/* ── boot typing ──────────────────────────────────────── */
function renderBoot(lines, partial) {
  bootBox.innerHTML = "";
  lines.forEach(([text, cls]) => {
    const p = document.createElement("p");
    p.className = cls;
    p.textContent = text;
    bootBox.appendChild(p);
  });
  if (partial) {
    const p = document.createElement("p");
    p.className = partial[1];
    p.textContent = partial[0];
    bootBox.appendChild(p);
  }
  const caret = document.createElement("span");
  caret.className = "caret";
  bootBox.appendChild(caret);
}

function runBoot() {
  clearTimeout(bootTimer);
  const src = BOOT[document.body.dataset.lang] || BOOT.en;
  if (reduceMotion) { renderBoot(src, null); return; }
  let li = 0, ci = 0;
  const step = () => {
    if (li >= src.length) { renderBoot(src, null); return; }
    const [text, cls] = src[li];
    if (ci < text.length) {
      ci += 1;
      renderBoot(src.slice(0, li), [text.slice(0, ci), cls]);
      bootTimer = setTimeout(step, text.startsWith("$") ? 26 : 16);
    } else {
      li += 1; ci = 0;
      renderBoot(src.slice(0, li), null);
      bootTimer = setTimeout(step, 240);
    }
  };
  renderBoot([], null);
  bootTimer = setTimeout(step, 320);
}

/* ── routing ──────────────────────────────────────────── */
// On GitHub Pages: swap this for hash routing (#/projects) or history routing
// with a 404.html copy of index.html. Screens are plain sections either way.
const NAV_FOR = { project: "projects" };

function go(route) {
  document.querySelectorAll("[data-screen]").forEach((s) => {
    s.hidden = s.dataset.screen !== route;
  });
  const navRoute = NAV_FOR[route] || route;
  document.querySelectorAll(".nav-btn").forEach((b) => {
    b.classList.toggle("is-active", b.dataset.route === navRoute);
  });
  window.scrollTo(0, 0);
  if (route === "home") runBoot();
}

document.addEventListener("click", (e) => {
  const el = e.target.closest("[data-route]");
  if (el) go(el.dataset.route);
});

/* ── language ─────────────────────────────────────────── */
document.querySelectorAll(".lang-btn").forEach((btn) => {
  btn.addEventListener("click", () => {
    document.body.dataset.lang = btn.dataset.lang;
    document.documentElement.lang = btn.dataset.lang;
    document.querySelectorAll(".lang-btn").forEach((b) => b.classList.toggle("is-active", b === btn));
    if (!document.querySelector('[data-screen="home"]').hidden) runBoot();
  });
});

/* ── theme ────────────────────────────────────────────────
   One attribute on <html> flips every token in styles.css.
   Stored choice wins; otherwise follow the OS setting. */
const themeBtn = document.querySelector("[data-theme-toggle]");

function applyTheme(theme) {
  document.documentElement.setAttribute("data-theme", theme);
  themeBtn.textContent = theme === "dark" ? "☀ light" : "☾ dark";
  themeBtn.title = theme === "dark" ? "Switch to light mode" : "Switch to dark mode";
  try { localStorage.setItem("aj-theme", theme); } catch (e) {}
}

let stored = null;
try { stored = localStorage.getItem("aj-theme"); } catch (e) {}
applyTheme(stored || (window.matchMedia("(prefers-color-scheme: light)").matches ? "light" : "dark"));

themeBtn.addEventListener("click", () => {
  const next = document.documentElement.getAttribute("data-theme") === "dark" ? "light" : "dark";
  applyTheme(next);
});

/* ── design notes (drop this block on the live site) ──── */
const notesBtn = document.querySelector("[data-notes]");
notesBtn.addEventListener("click", () => {
  const on = document.body.classList.toggle("notes-on");
  notesBtn.classList.toggle("is-on", on);
});

/* ── contact prompt ───────────────────────────────────── */
const chat = document.querySelector("[data-chat]");
const msgInput = document.querySelector("[data-msg]");

function sendMessage() {
  const msg = msgInput.value.trim();
  if (!msg) return;
  const mine = document.createElement("p");
  mine.className = "t-out";
  mine.textContent = "> " + msg;
  const reply = document.createElement("p");
  reply.className = "t-pink";
  reply.textContent = "opening your mail client — mailto:" + EMAIL;
  chat.append(mine, reply);
  msgInput.value = "";
  window.location.href =
    "mailto:" + EMAIL +
    "?subject=" + encodeURIComponent("Hello from the site") +
    "&body=" + encodeURIComponent(msg);
}

msgInput.addEventListener("keydown", (e) => { if (e.key === "Enter") sendMessage(); });
document.querySelector("[data-send]").addEventListener("click", sendMessage);

runBoot();
