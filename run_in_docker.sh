#!/bin/bash

SERVICE=$(cd $(dirname $0); pwd | awk -F '/' '{print $(NF)}')
MEMORY=512m


Start() {
    mem=$1
    echo ${mem}
    if [ -z "$mem" ];then
        mem=$MEMORY
    fi

    proc=$(ps -ef | grep ${SERVICE} | grep -v grep | wc -l)
    if [[ $proc != 0  ]];then
        exit 5
    fi

    java -server -Xms${mem} -Xmx${mem}  -jar /usr/app/${SERVICE}/${SERVICE}.jar  $ARGS
}


Stop() {

  while true; do
    proc=$(ps -ef | grep ${SERVICE} | grep -v grep | wc -l)
    if [[ $proc != 0 ]]; then
        echo "stop ${SERVICE} ..."
        /usr/bin/ps -ef | grep ${SERVICE} | grep -v grep | awk '{print $2}'| xargs kill -5
        sleep 1
    else
        echo "${SERVICE} not running now..."
        break
    fi
  done
}

Restart() {
    Stop
    Start
}


case $1 in
    start|run)
        Start $2
        ;;
    stop)
        Stop
        ;;
    restart)
        Restart
        ;;
esac
