# Production Core Integration

This repository has the following production-critical runtime path integrated:

1. Client requests a presigned S3-compatible upload URL.
2. Client uploads media directly to object storage (Cloudflare R2 in production, MinIO locally).
3. API creates a render job and publishes it to a Redis Stream consumer group.
4. FFmpeg workers run in an isolated container with a read-only root filesystem, no-new-privileges, dropped Linux capabilities, PID/memory/CPU limits and a bounded execution timeout.
5. Worker callbacks are authenticated with HMAC-SHA256 service-to-service signatures and a five-minute timestamp window.
6. API persists job state in PostgreSQL and broadcasts state/progress over WebSocket.
7. Completed outputs are exposed through short-lived presigned download URLs.

## Production settings

Use:
- Cloudflare R2/S3-compatible storage instead of MinIO.
- A managed Redis deployment with persistence and monitoring.
- PostgreSQL with automated backups and migrations.
- A private network between API, Redis and workers.
- A strong random `SERVICE_AUTH_SECRET` shared only by the API and worker fleet.
- Separate service credentials per environment.
- Android keystore through GitHub Secrets.
- iOS certificates/profiles through Fastlane Match.

## Important operational note

Redis Streams consumer groups provide at-least-once processing. A crashed worker can leave a message pending. This build keeps retry handling for job failures; a production fleet should also run an operational pending-message reclaimer/`XAUTOCLAIM` loop and alert on stuck pending entries.

## CI signing

Android release signing is wired to CI environment variables. iOS signing is wired through Fastlane Match, but the repository still requires the real Apple Xcode application shell/project and Apple credentials before a TestFlight archive can be produced.
