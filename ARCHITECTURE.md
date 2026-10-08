# Vietsub Video Studio — Production Core Integration

## Runtime

```text
Web/TMA ────────────────┐
Android / iOS / Desktop ├──> KMP Shared API Models
                        │
                        ▼
                 Ktor API :8080
                  │    │      │
             HMAC │    │      └── Telegram
                  │    │
                  │    ├── PostgreSQL/H2 + HikariCP
                  │    ├── S3/R2/MinIO object storage
                  │    └── Redis Streams
                  │             │
                  │             ▼
                  │        FFmpeg Worker pool
                  │         (non-root, isolated,
                  │          no-new-privileges,
                  │          timeout/output limits)
                  │             │
                  └<────────────┘  signed status callback
                        │
                        ▼
                 WebSocket progress hub
```

## Integrated production cores

1. **Object storage**: S3-compatible service with Cloudflare R2 / MinIO endpoint support, presigned PUT/GET, server-side multipart fallback.
2. **Queue**: Redis Streams + consumer group, retries and max-attempt control.
3. **Service-to-service authentication**: HMAC-SHA256 over service id, method, path, timestamp and body hash, with 5-minute clock-skew protection.
4. **FFmpeg worker isolation**: separate `:worker` process/container, non-root execution, capability drop, `no-new-privileges`, read-only filesystem, tmpfs workspace, timeout and output-size limits.
5. **Database**: HikariCP + PostgreSQL production / H2 local, idempotency key, job state, attempts, worker tracking and timestamps.
6. **Realtime**: Ktor WebSocket hub publishes every worker state update.
7. **Release signing**: Android CI decodes a base64 keystore and signs Release AAB; iOS uses Fastlane Match hooks; desktop builds are matrixed.
8. **Operations**: `/health`, `/ready`, `/status`, queue health, storage health and containerized local stack.

The original source architecture specifies Ktor as the API gateway, FFmpeg media processor, Telegram operations, Exposed/PostgreSQL persistence, WebSocket relay and multi-platform CI/CD; this implementation makes those cores executable rather than placeholders.
