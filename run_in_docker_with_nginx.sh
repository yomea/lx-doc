#!/bin/bash

SERVICE=$(cd $(dirname $0); pwd | awk -F '/' '{print $(NF)}')
SERVICE_DIR="/usr/app/${SERVICE}/${SERVICE}"
MEMORY=512m


Start() {
    mem=$1
    echo ${mem}
    if [ -z "$mem" ];then
        mem=$MEMORY
    fi

    proc=$(ps -ef | grep "$SERVICE_DIR" | grep -v grep | wc -l)
    if [[ $proc != 0  ]];then
        exit 5
    fi

    java -server -Xms${mem} -Xmx${mem}  -jar "$SERVICE_DIR".jar  $ARGS >> /usr/logs/${SERVICE}/${SERVICE}.log 2>&1 &
}

initStart() {
    # 后台模式启动 nginx，并指定nginx配置文件路径，如果使用了 docekr 部署，这个配置文件请映射到宿主机
    # 以便随时修改，每次修改之后可通过 docker exec -it 【容器id】 nginx -s reload -c /usr/nginx/config/nginx.conf
    # 重启 nginx
    nginx -g "daemon on;" -c /usr/nginx/config/nginx.conf

    Start $1

    # 避免容器退出
    tail -f /dev/null
}

Stop() {
    in_stop_count=0
     while true; do
        proc=$(ps -ef | grep "$SERVICE_DIR" | grep -v grep | wc -l)
        if [[ $proc != 0 ]]; then
            if [[ $in_stop_count == 0 ]]; then
              if ps -ef | grep "$SERVICE_DIR" | grep -v grep | awk '{print $1}'| xargs kill -5 > /dev/null; then
                echo "Attempt to gracefully close the ${SERVICE}"
              else
                echo "kill command exec error..."
                break
              fi
            elif [[ $in_stop_count -gt 30 ]]; then
                echo "Close timeout, Forcefully close the ${SERVICE}"
                ps -ef | grep "$SERVICE_DIR" | grep -v grep | awk '{print $1}'| xargs kill -9
                break
            else
                echo "Closed and waited for $in_stop_count seconds"
            fi
            in_stop_count=$((in_stop_count+1))
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
    initStart)
        initStart $2
        ;;
    stop)
        Stop
        ;;
    restart)
        Restart
        ;;
esac
