#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost:8080}"
curl --fail --silent --show-error "$BASE_URL/api/v1/health" | grep -q 'ok'
echo "health: ok"
if curl --fail --silent --show-error "$BASE_URL/api/v1/ready" >/dev/null; then
  echo "ready: ok"
else
  echo "ready: degraded (check Redis/storage)" >&2
  exit 2
fi
