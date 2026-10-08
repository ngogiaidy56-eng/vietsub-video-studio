# V3 Feature Matrix

| Layer | Feature | Implementation |
|---|---|---|
| Client | React PWA editor | `frontend/src` |
| Client | FFmpeg.wasm local render | `frontend/src/workers/ffmpegWorker.ts` |
| Client | Canvas subtitle hard-burn | `frontend/src/utils/subBurner.ts` |
| Client | Undo/Redo offline | `frontend/src/utils/undoEngine.ts` |
| Client | Smart Merge | `frontend/src/utils/smartMerge.ts` |
| Client | 4-channel audio mixer | `frontend/src/components/editor/AudioMixer.tsx` |
| Client | i18n | `frontend/src/services/i18n.ts`, `frontend/src/locales/*` |
| Client | PostHog analytics | `frontend/src/services/analytics.ts` |
| Client | PWA install/offline | `frontend/public/sw.js` |
| Cloud Edge | Telegram HMAC Web Crypto | `worker/src/auth/edgeAuth.ts` |
| Cloud Edge | D1 events | `worker/src/db/d1-client.ts` + migration |
| Cloud Edge | KV AI/event cache | `worker/src/index.ts` |
| Cloud Edge | R2 binding | `worker/wrangler.jsonc` |
| Cloud Edge | API proxy | `worker/src/index.ts` |
| Backend | Express API Gateway | `backend/src/server.ts` |
| Backend | Redis rate limiting | `backend/src/middlewares/rateLimiter.ts` |
| Backend | BullMQ queue/retry | `backend/src/queue/bullQueue.ts` |
| Backend | Background workers | `backend/src/queue/jobWorkers.ts` |
| Backend | WebSocket realtime | `backend/src/server.ts` |
| Backend | AI Gemini | `backend/src/services/vietsubAi.ts` |
| Backend | STT adapter | `backend/src/services/videoTranscriber.ts` |
| Backend | OCR adapter | `backend/src/services/imageTranslator.ts` |
| Backend | Subtitle extraction | `backend/src/services/extractor.ts` |
| Backend | R2/S3 presigned URLs | `backend/src/services/r2Storage.ts` |
| Backend | FFmpeg server render | `backend/src/services/renderService.ts` |
| Backend | ffprobe metadata | `backend/src/services/mediaPipeline.ts` |
| Backend | Postgres persistence | `backend/src/db/postgres.ts` |
| Bot | Telegram Webhook | `example_bot/handlers/*` |
| Bot | Stripe Checkout | `example_bot/handlers/payments.js` |
| Bot | Telegram Stars | `example_bot/handlers/payments.js` |
| DevOps | SOT | `config/sot.json`, `system-config/*` |
| DevOps | CI/CD | `.github/workflows/*` |
| DevOps | Semantic Release | `.releaserc.json` |
| DevOps | Docker isolation | `Dockerfile.worker`, `docker-compose.yml` |
| Native | Kotlin Multiplatform | `shared`, `composeApp`, `androidApp`, `iosApp`, `desktopApp` |
