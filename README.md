
## 理想文档

定位于个人和小团队的云文档。我们倡导私有化部署，数据掌握在自己手里，不用受制于人。

本仓库为后端仓库，前端代码仓库请移步：[wanglin2/lx-doc](https://github.com/wanglin2/lx-doc/tree/main)。

重要提示！！！：

1.不支持协同编辑；

2.不支持预览功能；

3.不支持手机端、客户端；

4.不提供在线服务，只做私有化部署；

## 部署

### 使用docker部署

如果您想自己使用docker构建镜像，那么可以参照 docker-build.sh 中的命令构建，但是前提您需要下载[maven](https://maven.apache.org/index)构建工具
和[java](https://www.oracle.com/java/technologies/downloads/#java8-linux)开发环境，如果您嫌麻烦，可以直接拉取本人在镜像仓库构建好的镜像

```shell
docker pull registry.cn-hangzhou.aliyuncs.com/wzh-yun/lx-doc:1.0
```

## 不使用容器部署



```shell
# maven 构建工具打包命令，执行该命令之后会在当前目录下生成一个叫target的目录，该目录下会生成一个 lx-doc.jar
mvn -DskipTests -U clean package

# 生成 lx-doc.jar 之后，可以运行以下脚本执行
sh run_no_in_docker.sh start 512m
```

由于本项目还使用到了redis哨兵模式和数据库




