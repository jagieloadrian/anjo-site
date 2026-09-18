#!/usr/bin/env python3
"""Static file server for the Playwright suite (research.md §5): serves the real
`kobwebExport` output, falling back to 404.html's *content* for any unmatched path — the
same behavior GitHub Pages uses in production (Error404.kt's own comment) — so a
client-side route like /projects/{unknown-slug} still boots the real app instead of
hitting a bare connection-refused/file-not-found response. Pure stdlib, no new dependency.
ponytail: the fallback response keeps HTTP 200 (stdlib http.server doesn't make sending a
custom status alongside an existing file's body a one-liner) rather than GH Pages' real
404 status — the client-side router only reads the URL, not the initial document's status,
so this doesn't affect anything the test suite actually checks.
"""
import http.server
import os
import sys


class SpaFallbackHandler(http.server.SimpleHTTPRequestHandler):
    def send_head(self):
        path = self.translate_path(self.path)
        # Kobweb's export layout puts a route's own page at "route.html" *and* (for a route with
        # dynamic children, e.g. /projects) a same-named directory for those children — so
        # "route.html" must be tried before falling through to directory-listing behavior, same as
        # GitHub Pages' real extension-less resolution.
        if not os.path.isdir(path) and os.path.exists(path):
            pass
        elif os.path.exists(path + ".html"):
            self.path = self.path + ".html"
        elif os.path.isdir(path):
            pass
        else:
            self.path = "/404.html"
        return super().send_head()


if __name__ == "__main__":
    port = int(sys.argv[1]) if len(sys.argv) > 1 else 4173
    directory = sys.argv[2] if len(sys.argv) > 2 else "."
    os.chdir(directory)
    http.server.test(HandlerClass=SpaFallbackHandler, port=port, bind="127.0.0.1")
