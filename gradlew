#!/bin/sh
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION="9.7.1"
CACHE_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/vietsub-distributions/gradle-${GRADLE_VERSION}"
DIST_DIR="$CACHE_DIR/gradle-${GRADLE_VERSION}"
if [ ! -x "$DIST_DIR/bin/gradle" ]; then
  mkdir -p "$CACHE_DIR"
  ZIP="$CACHE_DIR/gradle-${GRADLE_VERSION}-bin.zip"
  URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
  echo "Bootstrapping Gradle ${GRADLE_VERSION}..." >&2
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL --retry 3 "$URL" -o "$ZIP"
  elif command -v wget >/dev/null 2>&1; then
    wget -q --tries=3 "$URL" -O "$ZIP"
  else
    echo "curl or wget is required to bootstrap Gradle." >&2
    exit 1
  fi
  rm -rf "$CACHE_DIR/extract" "$DIST_DIR"
  mkdir -p "$CACHE_DIR/extract"
  unzip -q "$ZIP" -d "$CACHE_DIR/extract"
  mv "$CACHE_DIR/extract/gradle-${GRADLE_VERSION}" "$DIST_DIR"
  rm -rf "$CACHE_DIR/extract" "$ZIP"
fi
exec "$DIST_DIR/bin/gradle" -p "$APP_HOME" "$@"
