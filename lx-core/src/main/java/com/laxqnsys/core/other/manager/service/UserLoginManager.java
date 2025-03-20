package com.laxqnsys.core.other.manager.service;

import com.google.common.collect.Maps;
import com.laxqnsys.core.other.manager.model.UserLoginBO;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * @author wuzhenhong
 * @date 2024/5/22 11:23
 */
@Component
public class UserLoginManager {

    private Map<String, UserLoginBO> container = Maps.newConcurrentMap();
    private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public UserLoginManager() {
        service.scheduleWithFixedDelay(() -> {
            List<String> expireKeys = container.entrySet().stream().filter(entry -> {
                UserLoginBO loginBO = entry.getValue();
                long expireTime = loginBO.getExpireTime();
                long currentTime = System.currentTimeMillis();
                return currentTime > expireTime;
            }).map(Entry::getKey).collect(Collectors.toList());
            expireKeys.stream().forEach(container::remove);
        }, 1L, 1L, TimeUnit.SECONDS);
    }

    public void set(String key, String value, long expireTime) {
        long time = System.currentTimeMillis() + expireTime;
        UserLoginBO userLoginBO = UserLoginBO.builder()
            .value(value)
            .expireTime(time)
            .build();
        container.put(key, userLoginBO);
    }

    public String get(String key) {
        UserLoginBO userLoginBO = container.get(key);
        if (Objects.isNull(userLoginBO)) {
            return null;
        }
        long expireTime = userLoginBO.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (currentTime > expireTime) {
            this.delete(key);
            return null;
        }
        return userLoginBO.getValue();
    }

    public void delete(String key) {
        container.remove(key);
    }

    public void expire(String key, long expireTime) {
        UserLoginBO userLoginBO = container.get(key);
        if (Objects.nonNull(userLoginBO)) {
            long time = System.currentTimeMillis() + expireTime;
            userLoginBO.setExpireTime(time);
        }
    }

}
