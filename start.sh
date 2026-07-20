#!/usr/bin/env sh
# One command to build and run the Customer Portal (app + postgres + redis).
# Requires only Docker installed and running.
set -e
cd "$(dirname "$0")"
echo "Building and starting Customer Portal... (first run downloads images + builds, be patient)"
docker compose -f compose.prod.yaml up --build
