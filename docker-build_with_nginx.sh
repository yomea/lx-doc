#!/bin/bash

mvn -DskipTests -U clean package

mkdir -p /usr/logs/

docker build -t lx-doc:1.0 .

# 你可以拉取github上的代码进行构建
# 这里的 ARGS 参数可以直接写在 application-prod.yml 配置文件里，避免把密码登参数直接写在命令行中
# 挂在的宿主目录请先创建
# /usr/web/html/ 挂在的是前端资源目录
# /usr/config/lx-doc/ 挂在 lx-doc 启动的配置文件，比如 application-prod.yml 就放在这个目录下面
# /usr/nginx/config 放置nginx相关的配置
# /usr/logs/lx-doc 应用的日志
# /usr/attament/lx-doc 上传附件的存放路径
docker run -dit --network host -h lx-doc --privileged \
 -v /usr/web/html/:/usr/web/html/ \
 -v /usr/config/lx-doc/:/usr/config/lx-doc/ \
 -v /var/log/nginx/:/var/log/nginx/ \
 -v /usr/nginx/config:/usr/nginx/config \
 -v /usr/logs/lx-doc:/usr/logs/lx-doc \
 -v /usr/attament/lx-doc:/usr/attament/lx-doc \
 -e ARGS='--spring.profiles.active=prod --spring.config.location=classpath:/,/usr/config/lx-doc/ --app.name=lx-doc' \
--name lx-doc lx-doc:1.0


