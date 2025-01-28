#!/bin/sh
export IMAGE=$1
docker compose -f docker-compose.yml up -d
echo "success111"