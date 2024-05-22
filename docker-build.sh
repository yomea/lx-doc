#!/bin/bash

mvn -DskipTests -U clean package

mkdir -p /usr/logs/

docker build -t lx-doc:1.0 .

# 你可以拉取github上的代码进行构建，或者直接使用本来构建好的镜像  docker pull registry.cn-hangzhou.aliyuncs.com/wzh-yun/lx-doc:1.0
docker run -dit -p 9222:9222 -v /usr/logs/:/usr/logs/ -e ARGS=ARGS='--log.home=/usr/logs --loglevel=INFO --spring.datasource.druid.password=xx --spring.datasource.druid.url=jdbc:mysql://ip:3306/lx-doc?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true&useSSL=false&zeroDateTimeBehavior=convertToNull --spring.datasource.druid.username=username --spring.redis.password=xxx --spring.redis.sentinel.master=publicredis --spring.redis.sentinel.nodes=ip:26379,ip:26379,ip:26379' --name lx-doc lx-doc:1.0