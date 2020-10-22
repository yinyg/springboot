package tech.hiyinyougen.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author yinyg
 * @date 2020/10/19
 * @description
 */
@Service
public class RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Async
    public void testDistributedLockAsyncMethod() {
        System.out.println(stringRedisTemplate.opsForValue().setIfAbsent("testDistributedLock", "1", 5L, TimeUnit.SECONDS));
        try {
            Thread.sleep(1000L * 10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
