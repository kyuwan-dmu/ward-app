#!/bin/bash
set -euo pipefail

APP_DIR="${APP_DIR:-$HOME/ward-app}"

echo "[1/5] move to app dir: $APP_DIR"
cd "$APP_DIR"

echo "[2/5] pull latest main"
git pull --ff-only origin main

echo "[3/5] ensure gradle wrapper is executable"
chmod +x ./gradlew

echo "[4/5] build war"
./gradlew clean bootWar

echo "[5/5] build result"
ls -lh build/libs/*.war

LAST_COMMIT="$(git log -1 --pretty=format:'[%h] %s')"
echo "latest commit: $LAST_COMMIT"
