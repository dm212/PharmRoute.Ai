import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import test from "node:test";

async function render() {
  const workerUrl = new URL("../dist/server/index.js", import.meta.url);
  workerUrl.searchParams.set("test", `${process.pid}-${Date.now()}`);
  const { default: worker } = await import(workerUrl.href);

  return worker.fetch(
    new Request("http://localhost/", { headers: { accept: "text/html" } }),
    { ASSETS: { fetch: async () => new Response("Not found", { status: 404 }) } },
    { waitUntil() {}, passThroughOnException() {} },
  );
}

test("server-renders the PharmaTrace product shell", async () => {
  const response = await render();
  assert.equal(response.status, 200);
  assert.match(response.headers.get("content-type") ?? "", /^text\/html\b/i);

  const html = await response.text();
  assert.match(html, /<title>PharmaTrace \| Explainable medicine provenance<\/title>/i);
  assert.match(html, /PharmaTrace/);
  assert.match(html, /Follow every hand-off/);
  assert.match(html, /Medicine batch ID/);
  assert.match(html, /BT-2026-0812-A17/);
  assert.doesNotMatch(html, /Your site is taking shape|codex-preview|react-loading-skeleton/i);
});

test("frontend source contains live API and resilient UI states", async () => {
  const [page, layout, css, packageJson] = await Promise.all([
    readFile(new URL("../app/page.tsx", import.meta.url), "utf8"),
    readFile(new URL("../app/layout.tsx", import.meta.url), "utf8"),
    readFile(new URL("../app/globals.css", import.meta.url), "utf8"),
    readFile(new URL("../package.json", import.meta.url), "utf8"),
  ]);

  assert.match(page, /NEXT_PUBLIC_API_BASE_URL/);
  assert.match(page, /\/api\/v1\/batches\/\$\{encodeURIComponent\(requestedId\)\}\/investigation/);
  assert.match(page, /Traversing the supply graph/);
  assert.match(page, /Investigation unavailable/);
  assert.match(page, /No shipment events have been recorded/);
  assert.match(page, /No indirect exposure/);
  assert.match(layout, /PharmaTrace \| Explainable medicine provenance/);
  assert.match(css, /prefers-reduced-motion/);
  assert.match(css, /error-state/);
  assert.doesNotMatch(packageJson, /react-loading-skeleton/);
});
