#!/usr/bin/env bash
set -euo pipefail
# Run this once on a machine with Gradle installed if gradle-wrapper.jar is absent.
gradle wrapper --gradle-version 9.7.1
