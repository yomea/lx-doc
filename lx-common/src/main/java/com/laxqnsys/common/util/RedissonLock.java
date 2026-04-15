package com.laxqnsys.common.util;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import java.util.concurrent.Callable;
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

    private static final String LOCK_FAILED_MSG = "系统繁忙，请稍后重试！";

    private RedissonClient redissonClient;

    public RedissonLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, leaseTime, unit);
            if (!locked) {
                log.warn("获取分布式锁失败: lockKey={}, waitTime={}", lockKey, waitTime);
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), LOCK_FAILED_MSG);
            }
            runnable.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "操作被中断，请重试！");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void tryLock(String lockKey, long waitTime, TimeUnit unit, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, unit);
            if (!locked) {
                log.warn("获取分布式锁失败: lockKey={}, waitTime={}", lockKey, waitTime);
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), LOCK_FAILED_MSG);
            }
            runnable.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "操作被中断，请重试！");
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public Object tryLock(String lockKey, long waitTime, TimeUnit unit, Callable<Object> runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, unit);
            if (!locked) {
                log.warn("获取分布式锁失败: lockKey={}, waitTime={}", lockKey, waitTime);
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), LOCK_FAILED_MSG);
            }
            return runnable.call();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "操作被中断，请重试！");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "调用失败！", e);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
