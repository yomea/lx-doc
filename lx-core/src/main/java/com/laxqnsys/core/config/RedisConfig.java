package com.laxqnsys.core.config;

import java.util.ArrayList;
import java.util.List;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2023/8/9 15:55
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedissonClient redissonClient(RedisProperties properties) {
        Config config = new Config();
        List<String> nodes = properties.getSentinel().getNodes();
        List<String> newNodes = new ArrayList<>(nodes.size());

        for (String node : nodes) {
            newNodes.add(node.startsWith("redis://") ? node : "redis://" + node);
        }

        SentinelServersConfig serverConfig = config.useSentinelServers()
            .addSentinelAddress(newNodes.toArray(new String[nodes.size()]))
            .setMasterName(properties.getSentinel().getMaster())
            .setTimeout(5000)
            .setMasterConnectionPoolSize(10)
            .setSlaveConnectionPoolSize(10);

        if (!StringUtils.isEmpty(properties.getPassword())) {
            serverConfig.setPassword(properties.getPassword());
        }

        return Redisson.create(config);
    }

    @Bean
    public com.laxqnsys.common.util.RedissonLock redisLock(RedissonClient redissonClient) {
        return new com.laxqnsys.common.util.RedissonLock(redissonClient);
    }
}
