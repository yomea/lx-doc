package com.laxqnsys.common.util;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * @author wuzhenhong
 * @date 2023/8/9 16:58
 */
@Slf4j
public class RedissonLock {

    private RedissonClient redissonClient;

    public RedissonLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        if(Objects.isNull(lock)) {
            return;
        }
        try {
            if(lock.tryLock(waitTime, leaseTime, unit)) {
                runnable.run();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    public void tryLock(String lockKey, long waitTime, TimeUnit unit, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        if(Objects.isNull(lock)) {
            return;
        }
        try {
            if(lock.tryLock(waitTime, unit)) {
                runnable.run();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

}
