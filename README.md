
## 理想文档

定位于个人和小团队的云文档（支持流程图，脑图，PDF，Excel，Word，BPMN，markdown文档编辑）。我们倡导私有化部署，数据掌握在自己手里，不用受制于人。

本仓库为后端仓库，前端代码仓库请移步：[wanglin2/lx-doc](https://github.com/wanglin2/lx-doc/tree/main)。

本项目分为两个版本：
- 分支master使用了redis，主要是为了后续集群部署（后续需要接入文件服务器，有条件的开发者可以拉取当前代码自行扩展）
- 分支 personal 用于单机部署，不支持集群。

重要提示！！！：

1.不支持协同编辑；

2.不支持预览功能；

3.不支持手机端、客户端；

4.不提供在线服务，只做私有化部署；

## 环境准备

### 使用docker部署

如果您想自己使用docker构建镜像，那么可以参照 docker-build.sh 中的命令构建，但是前提您需要下载[maven](https://maven.apache.org/index)构建工具
和[java](https://www.oracle.com/java/technologies/downloads/#java8-linux)开发环境。

## 不使用容器部署

```shell
# maven 构建工具打包命令，执行该命令之后会在当前目录下生成一个叫target的目录，该目录下会生成一个 lx-doc.jar
mvn -DskipTests -U clean package

# 生成 lx-doc.jar 之后，可以运行以下脚本执行
sh run_no_in_docker.sh start 512m
```

在启动前，您还需要准备以下环境

- 安装mysql，mysql初始化脚本在本项目的doc.sql里，数据库名默认是lx-doc，如果有需要可以修改成自己的库名
- 安装redis，个人版本，可以忽略(请切换personal分支构建)，自己画画图，根本不需要这么麻烦，安装redis主要是为了解决高可用，分布式集群产生的问题（个人压根不需要集群，也没啥瓶颈）

## 配置说明
在启动应用之前，您还需要配置好参数才能启动应用，在项目里有一个叫做 application.yml 的配置文件，该配置预设了一些默认的参数，您只
需要关注以下参数（请仔细阅读注释）：
```yaml
lx:
  doc:
    whiteUrlList: /,/api/login,/api/register,/static/**,/assets/**,/system/error # 白名单url，配置之后将会被登录拦截器拦截
    fileUploadPath: /usr/attament/ # 配置文件上传的地址，单机使用时请配置
    
    # 如果您使用 nginx 来反向代理，那么不需要配置以下静态资源的映射，可以直接使用 nginx 来代理
    # 如果您不使用 nginx 想直接使用当前服务去请求，请将以下静态资源路径修改成自己的路径，然后在 lx.doc.whiteUrlList 
    # 添加 web 请求白名单，另外也要注意，如果您没有将静态资源打入镜像中，那么您需要在容器启动时进行 -v 目录挂载
    indexHtmlWebPath: /assets/index.html # 配置欢迎页
    staticResources:
      - pathPatterns: /static/** # 配置静态资源访问的web uri
        resourceLocations: file:///${lx.doc.fileUploadPath} # 配置静态资源所在物理磁盘的位置，不过静态资源的访问尽量使用 nginx 反向代理
      - pathPatterns: /assets/**
        resourceLocations: file:///D:/work/lx-doc/workbench/dist/assets/,file:///D:/work/lx-doc/workbench/dist/


spring:
  datasource:
    druid:
      maxActive: 20 # 最大连接池大小
      initialSize: 1 # 初始启动的连接数
      minIdle: 1 # 空闲时的连接数
      url: jdbc:mysql://127.0.0.1:3306/database_name?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true
      username: xxx
      password: xxx
  # 个人版，不使用集群登录的，不需要下面这个配置
  redis:
    sentinel:
      master: xxx
      nodes: redis1.domain:26379,redis2.domain:26379,redis3.domain:26379
    password: xxx
    lettuce:
      pool:
        max-active: 30
        min-idle: 8
        max-idle: 20
        time-between-eviction-runs: 30000
```

## 开源协议

[AGPL-3.0 License](https://github.com/yomea/lx-doc/blob/master/LICENSE)

本项目的开源协议为AGPL-3.0，简要描述就是您可以商用，但必须保留所使用开源项目的版权，并且源码也必须开源。当然，如果您不想开源，可以联系我们。

不过您也需要关注本项目所使用的项目的开源协议。




