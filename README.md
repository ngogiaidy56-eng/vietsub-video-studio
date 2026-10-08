# Vietsub Video Studio V3 — Cloud-Native & Edge

This repository combines the existing Kotlin Multiplatform/Compose stack with the V3 React PWA + Node.js/BullMQ + Cloudflare Edge architecture.

## Integrated cores

- React 19.3 + Vite 8 PWA editor with compact CapCut-style layout.
- FFmpeg.wasm client rendering worker and server FFmpeg fallback.
- Offline-first Service Worker + IndexedDB/local undo engine.
- i18n (vi/en) foundation, PostHog analytics hook.
- Express 5 API with Redis rate limiting, BullMQ jobs, AI translation, OCR adapter, STT adapter, R2/S3 presigned URLs.
- Cloudflare Worker with Web Crypto Telegram HMAC validation, KV caching, D1 event store and R2 binding.
- Telegram serverless webhook handlers and Stripe Checkout creation hook.
- CI/CD, Vitest, Playwright, semantic-release.
- Existing KMP modules retained under `shared`, `composeApp`, Android/iOS/Desktop; the old Kotlin worker is retained as `worker-kmp` while `worker/` is now the Cloudflare Edge worker.

## Development

```bash
cp .env.example .env
npm install
npm run dev
```

For production-like dependencies:

```bash
docker compose up -d redis postgres minio
npm --workspace backend run dev
npm --workspace backend run worker
npm --workspace frontend run dev
```

Do not commit API keys, Stripe secrets, bot tokens, keystores or Apple signing assets.
