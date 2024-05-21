#!/bin/bash

mvn -DskipTests -U clean package

mkdir -p /usr/logs/

docker build -t lx-doc:1.0 .

docker run -dit -p 9222:9222 -v /usr/logs/:/usr/logs/ -e ARGS='--log.home=/usr/logs --loglevel=INFO' --name lx-doc lx-doc:1.0