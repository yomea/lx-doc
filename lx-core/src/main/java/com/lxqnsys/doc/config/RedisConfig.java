package com.lxqnsys.doc.config;

import com.lxqnsys.doc.properties.RedissonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 * @author wuzhenhong
 * @date 2023/8/9 15:55
 */
@Configuration
@EnableConfigurationProperties(RedissonProperty.class)
@ConditionalOnClass({RedissonClient.class})
@ConditionalOnProperty("public.redis.sentinel.node")
public class RedisConfig {


    @Autowired
    private RedissonProperty redissonProperty;

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestWhileIdle(true);
        config.setMinEvictableIdleTimeMillis(60000);
        config.setTimeBetweenEvictionRunsMillis(30000);
        config.setNumTestsPerEvictionRun(-1);
        config.setMaxTotal(500);
        config.setMaxIdle(20);
        config.setMinIdle(8);
        return config;
    }

    @Bean
    public JedisSentinelPool jedisSentinelPool(JedisPoolConfig jedisPoolConfig) {
        HashSet<String> infos = new HashSet<>();
        String[] split = redissonProperty.getNode().split(",");
        infos.addAll(Arrays.asList(split));
        return new JedisSentinelPool(redissonProperty.getMaster(), infos, jedisPoolConfig, 5000,
            redissonProperty.getPassword());
    }

    @Bean
    public RedissonClient redissonClient(RedissonProperty properties) {
        Config config = new Config();
        String[] nodes = properties.getNode().split(",");
        List<String> newNodes = new ArrayList(nodes.length);

        for (String node : nodes) {
            newNodes.add(node.startsWith("redis://") ? node : "redis://" + node);
        }

        SentinelServersConfig serverConfig = config.useSentinelServers()
            .addSentinelAddress(newNodes.toArray(new String[nodes.length]))
            .setMasterName(properties.getMaster())
            .setTimeout(properties.getTimeout())
            .setMasterConnectionPoolSize(properties.getMasterSize())
            .setSlaveConnectionPoolSize(properties.getSlaveSize());

        if (!StringUtils.isEmpty(properties.getPassword())) {
            serverConfig.setPassword(properties.getPassword());
        }

        return Redisson.create(config);
    }

    @Bean
    public com.laxqnsys.doc.util.RedissonLock redisLock(RedissonClient redissonClient) {
        return new com.laxqnsys.doc.util.RedissonLock(redissonClient);
    }
}
