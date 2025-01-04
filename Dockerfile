# 拉取基础镜像
FROM openjdk:8-jdk-alpine
# 维护人
# MAINTAINER 951645267@qq.com
LABEL org.opencontainers.image.authors="951645267@qq.com"

ENV SERVICE=lx-doc

USER root

EXPOSE 9222

# 处理时区
RUN apk add tzdata  \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

# 创建目录
RUN mkdir -p /usr/app/${SERVICE}/ \
    && mkdir -p /usr/logs/${SERVICE}/ \
    && mkdir -p /usr/attament/${SERVICE}/

ADD run_in_docker.sh /usr/app/${SERVICE}/
# 如果是在windows下进行 docker 构建的话，要去掉 \r\n 换行符的\r
RUN sed -i 's/\r$//' /usr/app/${SERVICE}/*.sh
COPY lx-core/target/lx-doc.jar /usr/app/${SERVICE}/

WORKDIR /usr/app/${SERVICE}/

ENTRYPOINT ["sh", "run_in_docker.sh", "initStart"]