#!/bin/bash
export TAG=$1
docker compose up -d
echo "Server started..."