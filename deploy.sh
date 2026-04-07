#!/usr/bin/env bash
set -euo pipefail

REGISTRY="192.168.1.3:5005"
BE_IMAGE="dachserboard-be"
FE_IMAGE="dachserboard-fe"

echo "=== Building backend ==="
sudo docker buildx build --platform linux/amd64 -t "${BE_IMAGE}" .

echo "=== Building frontend ==="
sudo docker buildx build --platform linux/amd64 -t "${FE_IMAGE}" ./frontend

echo "=== Tagging ==="
sudo docker tag "${BE_IMAGE}" "${REGISTRY}/${BE_IMAGE}:latest"
sudo docker tag "${FE_IMAGE}" "${REGISTRY}/${FE_IMAGE}:latest"

echo "=== Pushing ==="
sudo docker push "${REGISTRY}/${BE_IMAGE}:latest"
sudo docker push "${REGISTRY}/${FE_IMAGE}:latest"

echo ""
echo "=== Done! Run the following on the server ==="
cat <<'EOF'
sudo docker stop dachserboard-be dachserboard-fe 2>/dev/null || true
sudo docker rm   dachserboard-be dachserboard-fe 2>/dev/null || true

sudo docker pull 192.168.1.3:5005/dachserboard-be:latest
sudo docker pull 192.168.1.3:5005/dachserboard-fe:latest

sudo docker run \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e MISTRAL_API_KEY=<api-key> \
  -e DB_USERNAME=<dbuser> \
  -e DB_PASSWORD=<dbpass> \
  -e APP_ENCRYPTION_KEY=<base64-key> \
  -d -p 8334:8334 --name dachserboard-be 192.168.1.3:5005/dachserboard-be:latest

sudo docker run -d -p 4300:80 --name dachserboard-fe 192.168.1.3:5005/dachserboard-fe:latest
EOF
