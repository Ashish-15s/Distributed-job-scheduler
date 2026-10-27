package com.scheduler.distributed_job_scheduler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    private static final DefaultRedisScript<Long> RELEASE_LOCK_SCRIPT =
            new DefaultRedisScript<>(
                    "if redis.call('get', KEYS[1]) == ARGV[1] " +
                            "then return redis.call('del', KEYS[1]) " +
                            "else return 0 end",
                    Long.class
            );

    public String acquireLock(String key, Duration expiry) {
        String token = UUID.randomUUID().toString();

        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, token, expiry);

        return Boolean.TRUE.equals(acquired) ? token : null;
    }

    public void releaseLock(String key, String token) {
        redisTemplate.execute(
                RELEASE_LOCK_SCRIPT,
                List.of(key),
                token
        );
    }
}