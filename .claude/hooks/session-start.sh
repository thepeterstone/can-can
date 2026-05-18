#!/bin/bash
set -euo pipefail

# Only run in remote Claude Code sessions
if [ "${CLAUDE_CODE_REMOTE:-}" != "true" ]; then
  exit 0
fi

echo '{"async": true, "asyncTimeout": 120000}'

cd "$CLAUDE_PROJECT_DIR"

# Set up git hooks
git config core.hooksPath scripts/git-hooks

# Gradle validation — download wrapper and verify project structure.
# Note: Google Maven (AGP / AndroidX) is not reachable from this environment.
# APK builds run via GitHub Actions (.github/workflows/build.yml).
./gradlew --version --no-daemon 2>&1 | head -5 || true
