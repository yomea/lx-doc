# 拉取基础镜像
FROM openjdk:8-jdk-alpine
# 维护人
MAINTAINER 951645267@qq.com

ENV SERVICE lx-doc

USER root

EXPOSE 9222

RUN mkdir -p /usr/app/${SERVICE}/
RUN mkdir -p /usr/logs/${SERVICE}/
RUN mkdir -p /usr/attament/${SERVICE}/

ADD run_in_docker.sh /usr/app/${SERVICE}/
COPY lx-core/target/lx-doc.jar /usr/app/${SERVICE}/

WORKDIR /usr/app/${SERVICE}/

ENTRYPOINT ["sh", "run_in_docker.sh", "start"]