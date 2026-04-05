#!/bin/bash
set -euo pipefail

APP_DIR="${APP_DIR:-$HOME/ward-app}"
WAR_FILE="$(ls -t "$APP_DIR"/build/libs/*.war 2>/dev/null | head -n 1)"

if [[ -z "$WAR_FILE" || ! -f "$WAR_FILE" ]]; then
	echo "WAR file not found under $APP_DIR/build/libs"
	exit 1
fi

echo "[1/5] remove exploded ROOT"
sudo rm -rf /var/lib/tomcat10/webapps/ROOT

echo "[2/5] deploy new war"
sudo cp "$WAR_FILE" /var/lib/tomcat10/webapps/ROOT.war

echo "[3/5] restart tomcat"
sudo systemctl restart tomcat10

echo "[4/5] check tomcat status"
sudo systemctl --no-pager status tomcat10

echo "[5/5] deployed war: $WAR_FILE"
