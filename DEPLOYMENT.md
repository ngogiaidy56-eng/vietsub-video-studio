# Production deployment

## 1. Local integration stack

```bash
docker compose up --build
```

Provision the MinIO bucket `vietsub` once, or replace the endpoint with Cloudflare R2 credentials.

## 2. Cloudflare R2

Set:

```dotenv
STORAGE_ENDPOINT=https://<account-id>.r2.cloudflarestorage.com
STORAGE_REGION=auto
STORAGE_BUCKET=vietsub
STORAGE_ACCESS_KEY=<r2-access-key>
STORAGE_SECRET_KEY=<r2-secret>
STORAGE_PUBLIC_BASE_URL=https://cdn.example.com
```

## 3. PostgreSQL + Redis

Production server should use a managed PostgreSQL and managed Redis. Keep `REDIS_RENDER_GROUP` identical for all worker replicas.

## 4. Service authentication

Generate one high-entropy `SERVICE_AUTH_SECRET` and inject the same value into the API and FFmpeg worker secret stores. Do not commit it.

## 5. Android signing

GitHub Actions expects:

- `ANDROID_KEYSTORE_B64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

## 6. iOS signing

Configure Fastlane Match secrets and certificates outside Git. The pipeline intentionally separates framework compilation from Apple signing/publishing because those credentials belong to App Store Connect / Match storage.

## 7. Security baseline

- API/worker communication uses HMAC-signed internal callbacks.
- FFmpeg runs in a dedicated worker instead of the API process.
- Worker containers run as non-root, drop Linux capabilities and use a read-only root filesystem.
- User object keys are validated against traversal and unsafe characters.
- Never log API keys, service secrets, Telegram bot tokens or signing secrets.

## 8. Render request example

1. Request a presigned URL:

```bash
curl -X POST "$BASE_URL/api/v1/uploads/presign" \\
  -H 'Content-Type: application/json' \\
  -d '{"fileName":"input.mp4","contentType":"video/mp4"}'
```

2. Upload the media directly to the returned `uploadUrl` with HTTP PUT.

3. Submit the render job:

```bash
curl -X POST "$BASE_URL/api/v1/render" \\
  -H 'Content-Type: application/json' \\
  -d '{
    "inputObjectKey":"uploads/<id>-input.mp4",
    "subtitleObjectKey":"uploads/<id>-subtitle.srt",
    "fontFamily":"Arial",
    "fontSize":34,
    "bold":true,
    "primaryColor":"&H00FFFFFF",
    "outlineColor":"&H00000000",
    "outlineWidth":2,
    "alignment":2,
    "marginV":42
  }'
```

4. Subscribe to `/ws/jobs/{jobId}` for progress. The job endpoint returns a fresh presigned output URL once rendering is complete.
