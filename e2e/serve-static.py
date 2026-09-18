#!/usr/bin/env python3
# SPA fallback to 404.html content, matching GitHub Pages behavior, for unmatched routes.
import http.server
import os
import sys


class SpaFallbackHandler(http.server.SimpleHTTPRequestHandler):
    def send_head(self):
        path = self.translate_path(self.path)
        # route.html must be tried before directory-listing, since /projects has both.
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
