#!/bin/bash
set -e

echo "=== Pulling latest changes on devops branch ==="
git pull origin devops

echo "=== Building Frontend ==="
npm install
npm run build

echo "=== Building Backend ==="
cd opportunity360-backend
chmod +x mvnw
./mvnw clean package -DskipTests
cd ..

echo "=== Restarting Spring Boot Service ==="
sudo systemctl restart opportunity360

echo "=== Reloading Nginx ==="
sudo systemctl reload nginx

echo "=== Deployment Complete ==="
