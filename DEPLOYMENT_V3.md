# V3 Production Deployment

## Cloudflare Edge
`worker/wrangler.jsonc` binds D1 + KV + R2. Replace placeholder IDs and use `wrangler secret put TELEGRAM_BOT_TOKEN` and `wrangler secret put EDGE_SHARED_SECRET`.

Cloudflare recommends `wrangler.jsonc` for new Workers configurations and supports KV/R2/D1 bindings directly. See Cloudflare Workers configuration docs.

## Backend
Set `DATABASE_URL`, Redis, Gemini, R2 and payment secrets. Run the API and a separate BullMQ worker pool. Keep FFmpeg workers isolated in containers with CPU/RAM/PID limits.

## Frontend
Set `VITE_API_BASE_URL`, `VITE_WS_BASE_URL`, and optional PostHog variables. The PWA caches its shell and FFmpeg WASM assets after first use.
