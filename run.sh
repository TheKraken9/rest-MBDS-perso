#!/bin/bash
# Build tous les modules puis lance docker-compose
set -e

SERVICES=("eureka-server" "config-server" "dataset-manager-service" "generator-service" "api-gateway")

echo "=== Build de tous les modules ==="
for SERVICE in "${SERVICES[@]}"; do
    echo "--- Building :${SERVICE}:bootJar ---"
    ./gradlew ":${SERVICE}:clean" ":${SERVICE}:bootJar"
    echo "SUCCESS: ${SERVICE} construit."
done

echo ""
echo "=== Lancement avec Docker Compose ==="
docker compose up --build
