#!/bin/sh

APP=$(cd $(dirname $0); pwd | awk -F '/' '{print $(NF)}')

echo $APP

mvn -DskipTests -U clean package
cd target

# 拷贝jar包
mkdir -p $APP/lib
cp $APP.jar $APP/lib

# 拷贝执行文件
mkdir -p $APP/bin
cp ../bin/run.sh $APP/bin

# 拷贝配置
mkdir -p $APP/conf

# 打zip包
zip -r $APP.zip $APP/*

echo "package $APP.zip success !"