
## 简介

~~在线体验地址（随时会被释放，请勿正式使用，释放后数据将不存在）：http://47.97.229.115:8089（已释放）~~

完整文档地址：[理想文档](https://wanglin2.github.io/lx-doc-site/)

理想文档定位于个人和小团队的云文档（支持流程图，脑图，PDF，Excel，Word，BPMN，markdown文档编辑）。我们倡导私有化部署，数据掌握在自己手里，不用受制于人。

本仓库为后端仓库，前端代码仓库请移步：[wanglin2/lx-doc](https://github.com/wanglin2/lx-doc/tree/main)。

本项目分为两个版本：
- 分支 master 用于分布式，支持集群部署
- 分支 personal 或 back_front_whole（前后端不分离） 用于单机部署，不支持集群。

重要提示！！！：

1.不会支持协同编辑；

2.不会支持手机端、客户端；

3.不会提供在线服务，只做私有化部署；

## 项目简要说明

后端模块目前简单的分为了 lx-common 与 lx-core 模块：

- lx-common：主要放一些通用的类，为后续可能得多模块做准备
- lx-core：核心的实现类，比如控制层，服务层，数据库操作，启动类都在这个目录

在该仓库的根目录下还有以下文件：

- application-prod.yml：该文件是生产配置文件，主要配置数据库，redis，除系统属性与系统变量之外，该配置文件优先级比 lx-core
  模块下的 application.yml（该文件配置了一些默认的属性） 优先级更高，所以如果您有需求，您可以在这个文件里配置属性，覆盖
  application.yml 中的默认属性
- doc.sql：理想文档的数据库脚本，该脚本是mysql的sql脚本，默认的数据库名为 lx_doc ，您可在自己的数据库中配置该名称，也可以修改成您想要的数据库名
- docker-build*.sh：提供了一些打包，容器操作的命令示例
- Dockerfile*：docker镜像打包示例
- nginx.conf：nginx的配置文件，如果您需要使用nginx做反向代理，可以直接使用该文件，如果你有自己的配置偏好，那么可以进行微调
- run_*.sh：启动脚本

目前该项目分为两个版本的分支，分别是分布式版本与个人版本：

- 分布式版本：该版本在 master 分支上，目前该版本的上传功能默认实现了 minio，oss未实现，如果有需要可以联系我们。
  数据库没有做分库分表，在数量很大，读写压力也很大的情况下，可考虑通过用户分库分表，视场景而定
- 个人版本：该版本在 personal 或 back_front_whole（前后端不分离） 分支上，仅支持单机部署，附件和文档内容默认都是保存在本地磁盘上

如果你只是给自己或者朋友们平时画画图，导出图片啊之类的操作，也没什么高并发，高可用，性能方面的要求，
那么直接使用个人版本部署一台机器即可，个人版部署非常简单，只要一台机器，装个mysql就完事了，不用搞那么多的花里胡哨。


## 部署

### 使用docker部署

如果您想自己使用docker构建镜像，那么可以参照 docker-build.sh 中的命令构建，但是前提您需要下载[maven](https://maven.apache.org/index)构建工具
和[OpenJDK](https://jdk.java.net/java-se-ri/11-MR3)开发环境。

### 不使用容器部署

```shell
# maven 构建工具打包命令，执行该命令之后会在当前目录下生成一个叫target的目录，该目录下会生成一个 lx-doc.jar
mvn -DskipTests -U clean package

# 生成 lx-doc.jar 之后，可以运行以下脚本执行
sh run_no_in_docker.sh start 512m
```

在启动前，您还需要准备以下环境

- 安装mysql，mysql初始化脚本在本项目的doc.sql里，数据库名默认是lx_doc，如果有需要可以修改成自己的库名
- 安装redis，个人版本，可以忽略(请切换personal分支构建)，自己画画图，根本不需要这么麻烦，安装redis主要是为了解决高可用，分布式集群产生的问题（个人压根不需要集群，也没啥瓶颈）

### 配置说明
在启动应用之前，您还需要配置好参数才能启动应用，在项目里有一个叫做 application.yml 的配置文件，该配置预设了一些默认的参数，您只
需要关注以下参数（请仔细阅读注释）：
```yaml
lx:
  doc:
    docStorage:
      type: minio # 文档存储类型，目前已实现的有 dataBase，local，未实现的 oss，minio，接口：com.laxqnsys.core.doc.service.IDocFileContentStorageService
      path: ${app.name}/doc/content/ # 文档内容存储的位置，如果类型是 dataBase ，此属性无效
      minio:
        endpoint: http://localhost:9000
        bucket: test
        accessKey: xxx
        secretKey: yyy
    whiteUrlList: /,/api/login,/api/register,/static/**,/assets/**,/system/error # 白名单url，配置之后将会被登录拦截器拦截
    fileUpload:
      type: minio # 文件上传类型，目前已实现local，为实现 oss，minio，接口 com.laxqnsys.core.sys.service.ISysFileUploadService
      path: ${app.name}/attachment/ # 配置文件上传的地址，设置为 local 时，该属性才有效
      minio:
        endpoint: http://localhost:9000
        bucket: test
        accessKey: xxx
        secretKey: yyy
    
    # 如果您使用 nginx 来反向代理，那么不需要配置以下静态资源的映射，可以直接使用 nginx 来代理
    # 如果您不使用 nginx 想直接使用当前服务去请求，请将以下静态资源路径修改成自己的路径，然后在 lx.doc.whiteUrlList 
    # 添加 web 请求白名单，另外也要注意，如果您没有将静态资源打入镜像中，那么您需要在容器启动时进行 -v 目录挂载
    indexHtmlWebPath: /assets/index.html # 配置欢迎页
    staticResources:
      - pathPatterns: /static/** # 配置静态资源访问的web uri
        resourceLocations: file:///${lx.doc.fileUpload.path} # 配置静态资源所在物理磁盘的位置，不过静态资源的访问尽量使用 nginx 反向代理
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
如果您不想修改 application.yml 配置文件，以上配置都可以通过启动参数指定，docker 启动时，可以通过 -e ARGS='--lx.doc.whiteUrlList=/,/api/login,/api/register,/static/**,/assets/**,/system/error' 指定,
也可以写到 application-prod.yml 的配置文件中，避免参数太长，不直观，启动参数配置可参考如下示例：
本项目提供了三种docker部署方案，分别对应项目根目录下的 Dockerfile，Dockerfile_with_nginx，Dockerfile_whole

- Dockerfile：JDK环境
- Dockerfile_with_nginx：nginx，JDK环境
- Dockerfile_whole：带有mysql，nginx，JDK环境，将前端web文件，sql脚本，nginx配置文件，应用配置文件一并打入镜像,可以不修改任何配置，一键启动即可

下面是 Dockerfile_with_nginx 打出来的镜像的容器启动命令说明（每种方案对应的Dockerfile都有一个对应docker-build*.sh，可以
直接参考该文件里的docker启动命令）：

```shell
# 你可以拉取github上的代码进行构建
# 这里的 ARGS 参数可以直接写在 application-prod.yml 配置文件里，避免把密码登参数直接写在命令行中
# 挂在的宿主目录请先创建
# /usr/web/html/ 挂在的是前端资源目录
# /usr/config/lx-doc/ 挂在 lx-doc 启动的配置文件，比如 application-prod.yml 就放在这个目录下面
# /usr/nginx/config 放置nginx相关的配置
# /usr/logs/lx-doc 应用的日志
# /usr/attachment/lx-doc 上传附件的存放路径
docker run -d --network host --privileged \
 -v /usr/web/html/:/usr/web/html/ \
 -v /usr/config/lx-doc/:/usr/config/lx-doc/ \
 -v /var/log/nginx/:/var/log/nginx/ \
 -v /usr/nginx/config:/usr/nginx/config \
 -v /usr/logs/lx-doc:/usr/logs/lx-doc \
 -v /usr/attachment/lx-doc:/usr/attachment/lx-doc \
 -e ARGS="--spring.profiles.active=prod --spring.config.location=classpath:/,/usr/config/lx-doc/ --app.name=lx-doc" \
 -e MEMORY=1024m \
--name lx-doc lx-doc:1.0

```

注意：如果你的 docker 版本较低，可能不支持 --network host，这个命令的意思是容器共享宿主机的网络，如果你的docker版本不支持，
可以看到的现象就是你在宿主机上看不到容器启动的端口号，或者你直接执行 docker exec -d 容器id ifconfig 得到的ip地址和宿主机
不一致，那么这个时候你就不要使用 --network host 这个命令，你手动映射端口，除此之外如果使用了域名去连接mysql或者redis的可以改成
ip地址或者修改容器的hosts，修改hosts可以在Dockerfile操作：

```shell
# 你可以拉取github上的代码进行构建
# 这里的 ARGS 参数可以直接写在 application-prod.yml 配置文件里，避免把密码登参数直接写在命令行中
# 挂在的宿主目录请先创建
# /usr/web/html/ 挂在的是前端资源目录
# /usr/config/lx-doc/ 挂在 lx-doc 启动的配置文件，比如 application-prod.yml 就放在这个目录下面
# /usr/nginx/config 放置nginx相关的配置
# /usr/logs/lx-doc 应用的日志
# /usr/attachment/lx-doc 上传附件的存放路径
docker run -d -p 9222:9222 -p 8089:8089 --privileged \
 -v /usr/web/html/:/usr/web/html/ \
 -v /usr/config/lx-doc/:/usr/config/lx-doc/ \
 -v /var/log/nginx/:/var/log/nginx/ \
 -v /usr/nginx/config:/usr/nginx/config \
 -v /usr/logs/lx-doc:/usr/logs/lx-doc \
 -v /usr/attachment/lx-doc:/usr/attachment/lx-doc \
 -e ARGS="--spring.profiles.active=prod --spring.config.location=classpath:/,/usr/config/lx-doc/ --app.name=lx-doc" \
 -e MEMORY=1024m \
--name lx-doc lx-doc:1.0

```

## 扩展开发

目前该项目分为两个版本的分支，分别是分布式版本（master）与个人版本（personal或back_front_whole（前后端不分离）），您根据自己的需求选择分支做扩展。
该项目使用 Maven 做包依赖与管理工具，所以您的开发环境需要配置JDK8和Maven3.0及以上版本。

另外，该项目还有以下需要待优化和扩展的建议：
- master 分支分布式版本的上传文件的分布式存储默认集成MINIO，如果您不想使用MINIO，想使用其他的分布式存储系统，比如OSS，请自行实现对应的接口。
- 文档内容目前实现了数据库存储、本地存储（单机用）和 MINIO 三种（可通过lx.doc.docStorage.type指定存储方式）方案，但数据库存储的文档在内容比较大的时候，容易受数据库字段长度和 mysql max_allowed_packet 的
  的制约，另外使用数据库保存大文件数据时性能也比较差，为了提高并发能力和性能可以考虑使用文件系统存储（分布式可以使用OSS和MINIO，
  单机可以本地存储）。
- 目前文档搜索只支持文档标题的搜索，不支持文档内容的搜索，如果您需要支持文档内容的搜索，可以考虑引入 ES ，将文档内容同步给它。

## 开源协议

[AGPL-3.0 License](https://github.com/yomea/lx-doc/blob/master/LICENSE)

本项目的开源协议为AGPL-3.0，简要描述就是您可以商用，但必须保留所使用开源项目的版权，并且源码也必须开源。当然，如果您不想开源，可以联系我们。

不过您也需要关注本项目所使用的项目的开源协议。




