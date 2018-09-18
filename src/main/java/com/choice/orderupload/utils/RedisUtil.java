package com.choice.orderupload.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author 林金成
 * @date 2018/8/20 9:21
 */
@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String get(String key) {
        return stringRedisTemplate
                .opsForValue()
                .get(key);
    }

    public void set(String key, String value, int i) {
        stringRedisTemplate
                .opsForValue()
                .set(key, value, i, TimeUnit.SECONDS);
    }

    public void setMap(String key, Map<Object, Object> map, long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForHash().putAll(key, map);
        stringRedisTemplate.expire(key, timeout, timeUnit);
    }

    public Map<Object, Object> getMap(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public void addToSet(String key, String... value) {
        stringRedisTemplate.opsForSet().add(key, value);
    }

    public Long delFromSet(String key, String... value) {
        return stringRedisTemplate.opsForSet().remove(key, value);
    }

    public boolean isSetEmpty(String key) {
        return !(stringRedisTemplate.hasKey(key) && stringRedisTemplate.opsForSet().size(key) > 0);
    }

    public boolean containsInSet(String key, String value) {
        return stringRedisTemplate.opsForSet().isMember(key, value);
    }

    public Set<String> getSet(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return stringRedisTemplate.expire(key, timeout, timeUnit);
    }

    public boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public boolean setIfAbsent(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }
}