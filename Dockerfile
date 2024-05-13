FROM hub.uban360.com/library/jdk:jre-8u241

ENV SERVICE lx-doc
ENV MEMORY  512
ENV ARGS=

USER root

COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas

RUN rpm -ivh https://mirrors.aliyun.com/centos/7/os/x86_64/Packages/unzip-6.0-20.el7.x86_64.rpm

COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas

RUN mkdir -p /home/admin/

ADD ${SERVICE}.zip /home/admin/

RUN unzip -o /home/admin/${SERVICE}.zip -d /home/admin/

#RUN sed -i 's/9980/8080/g' /home/admin/${SERVICE}/config/application.yml

ADD run.sh /home/admin/${SERVICE}/

WORKDIR /home/admin/${SERVICE}

ENTRYPOINT ["sh","run.sh"]