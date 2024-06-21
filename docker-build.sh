#!/bin/bash

mvn -DskipTests -U clean package

mkdir -p /usr/logs/

docker build -t lx-doc:1.0 .

# 你可以拉取github上的代码进行构建
# 这里的 ARGS 参数可以直接写在 application-prod.yml 配置文件里，避免把密码登参数直接写在命令行中
# 挂在的宿主目录请先创建
# /usr/config/lx-doc/ 挂在 lx-doc 启动的配置文件，比如 application-prod.yml 就放在这个目录下面
# /usr/logs/lx-doc 应用的日志
# /usr/attament/lx-doc 上传附件的存放路径
docker run -dit --network host -h lx-doc --privileged \
 -v /usr/config/lx-doc/:/usr/config/lx-doc/ \
 -v /usr/logs/lx-doc:/usr/logs/lx-doc \
 -v /usr/attament/lx-doc:/usr/attament/lx-doc \
 -e ARGS='--spring.profiles.active=prod --spring.config.location=classpath:/,/usr/config/lx-doc/ --app.name=lx-doc' \
--name lx-doc lx-doc:1.0

# 查看容易打印日志
docker logs -f [containerId]

# 更新了nginx.conf,需要重启nginx，命令如下：
docker ps docker ps | grep 'lx-doc'
docker exec -it [containerId] nginx -s reload -c /usr/nginx/config/nginx.conf

# 修改了 application-prod.yml ，需重启容器
docker restart [containerId]

# 停止容器
docker stop [containerId]

# 清除所有停止的容器
docker container prune
# 删除指定容器
docker rm [containerId or name]

# 删除镜像
docker rmi [imageId or tag]

# 推送镜像到仓库
docker login --username=用户名 registry.cn-hangzhou.aliyuncs.com
docker tag [ImageId] registry.cn-hangzhou.aliyuncs.com/wzh-yun/lx-doc:[镜像版本号]
docker push registry.cn-hangzhou.aliyuncs.com/wzh-yun/lx-doc:[镜像版本号]
docker logout registry.cn-hangzhou.aliyuncs.com

# 拉取镜像
docker pull registry.cn-hangzhou.aliyuncs.com/wzh-yun/lx-doc:[镜像版本号]