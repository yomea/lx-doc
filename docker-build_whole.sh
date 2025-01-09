#!/bin/sh

mvn -DskipTests -U clean package

mkdir -p /usr/logs/

docker build -t lx-doc:1.0 .

# 你可以拉取github上的代码进行构建
# 挂在的宿主目录请先创建
# /usr/logs/lx-doc 应用的日志
# /usr/attament/lx-doc 上传附件的存放路径
# /usr/app/mysql mysq存放数据路径
docker run -d --network host --privileged \
 -v /var/log/nginx/:/var/log/nginx/ \
 -v /usr/logs/lx-doc:/usr/logs/lx-doc \
 -v /usr/attament/lx-doc:/usr/attament/lx-doc \
 -v /usr/app/mysql:/usr/app/mysql \
 -e ARGS="--MYSQL_IP=host.docker.internal --spring.profiles.active=prod --spring.config.location=classpath:/,/usr/config/lx-doc/ --app.name=lx-doc" \
 -e MEMORY=1024m \
--name lx-doc \
--add-host host.docker.internal:host-gateway \
lx-doc:1.0

可以看到以上启动命令中使用了
--add-host host.docker.internal:host-gateway
添加hosts，这是因为需要通过容器内部去访问mysql，除此之外，还可以通过以下命令去桥接网络，
docker network create -d bridge --subnet 192.168.0.0/24 --gateway 192.168.0.1 localNet
那么对应 application-prod.yml
的mysql的ip地址请改成 192.168.0.1，也可以连接到mysql
如果以上这些方法都没法访问到数据库，那么可以在 -e ARGS 这个参数里添加 --MYSQL_IP=ip地址

# 注意：如果你的 docker 版本较低或者是其他原因（比如windows系统或者其他操作系统可能不一样），可能不支持 --network host，这个命令的意思是容器共享宿主机的网络，如果你的docker版本不支持，
# 可以看到的现象就是你在宿主机上看不到容器启动的端口号，或者你直接执行 docker exec -d 容器id ifconfig 得到的ip地址和宿主机
# 不一致，那么这个时候你就不要使用 --network host 这个命令，你手动映射端口，除此之外如果使用了域名去连接mysql或者redis的可以改成
# ip地址或者通过 --add-host 域名:ip地址：

# 你可以拉取github上的代码进行构建
# 挂在的宿主目录请先创建
# /usr/logs/lx-doc 应用的日志
# /usr/attament/lx-doc 上传附件的存放路径
# /usr/app/mysql mysq存放数据路径
docker run -d -p 9222:9222 -p 8089:8089 -p 3306:3306 --privileged \
 -v /var/log/nginx/:/var/log/nginx/ \
 -v /usr/logs/lx-doc:/usr/logs/lx-doc \
 -v /usr/attament/lx-doc:/usr/attament/lx-doc \
 -v /usr/app/mysql:/usr/app/mysql \
 -e ARGS="--MYSQL_IP=127.0.0.1 --spring.profiles.active=prod --spring.config.location=classpath:/,/usr/config/lx-doc/ --app.name=lx-doc" \
 -e MEMORY=1024m \
--name lx-doc \
lx-doc:1.0

# 重启应用
docker exec -d [containerId] sh /usr/app/lx-doc/run_in_docker_whole.sh restart
# 关闭应用
docker exec -d [containerId] sh /usr/app/lx-doc/run_in_docker_whole.sh stop
# 启动应用
docker exec -d [containerId] sh /usr/app/lx-doc/run_in_docker_whole.sh start 512
或
docker exec -d [containerId] sh /usr/app/lx-doc/run_in_docker_whole.sh run 512

# 查看容易打印日志
docker logs -f [containerId]

# 更新了nginx.conf,需要重启nginx，命令如下：
docker ps docker ps | grep 'lx-doc'
docker exec -d [containerId] nginx -s reload -c /usr/nginx/config/nginx.conf

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
