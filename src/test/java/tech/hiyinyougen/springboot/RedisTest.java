package tech.hiyinyougen.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import tech.hiyinyougen.springboot.domain.User;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author yinyg
 * @date 2020/9/14
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testStringRedisTemplate() {
        try {
            System.out.println("first: " + stringRedisTemplate.opsForValue().setIfAbsent("testStringRedisTemplate", "hello"));
            System.out.println("second: " + stringRedisTemplate.opsForValue().setIfAbsent("testStringRedisTemplate", "world"));
            System.out.println("result: " + stringRedisTemplate.opsForValue().get("testStringRedisTemplate"));

            stringRedisTemplate.opsForHash().put("testStringRedisTemplateForHash", "name", "yinyg");
            stringRedisTemplate.opsForHash().put("testStringRedisTemplateForHash", "age", "20");
            System.out.println("testStringRedisTemplateForHash result: "
                    + "name=" + stringRedisTemplate.opsForHash().get("testStringRedisTemplateForHash", "name")
                    + ", age=" + stringRedisTemplate.opsForHash().get("testStringRedisTemplateForHash", "age"));
        } finally {
            stringRedisTemplate.delete(Arrays.asList("testStringRedisTemplate", "testStringRedisTemplateForHash"));
        }
    }

    @Test
    public void testRedisTemplate() {
        try {
            redisTemplate.opsForValue().set("testRedisTemplate", User.builder().username("zhangsan").age(18).build());
            System.out.println("result: " + redisTemplate.opsForValue().get("testRedisTemplate"));

            redisTemplate.opsForValue().set("testRedisTemplateForBoolean", Boolean.TRUE);
            System.out.println("testRedisTemplateForBoolean result: " + redisTemplate.opsForValue().get("testRedisTemplateForBoolean"));
        } finally {
            redisTemplate.delete(Arrays.asList("testRedisTemplate", "testRedisTemplateForBoolean"));
        }
    }

    @Test
    public void testRedisTemplateForList() {
        try {
            redisTemplate.opsForList().rightPush("testRedisTemplateForList", "right");
            redisTemplate.opsForList().rightPush("testRedisTemplateForList", "right2");
            redisTemplate.opsForList().leftPush("testRedisTemplateForList", "left");
            redisTemplate.opsForList().leftPush("testRedisTemplateForList", "left2");
            List<Object> list = redisTemplate.opsForList().range("testRedisTemplateForList", 0, -1);
            System.out.print("result:");
            if (!CollectionUtils.isEmpty(list)) {
                for (Object s : list) {
                    System.out.print(" " + s);
                }
            }
        } finally {
            redisTemplate.delete("testRedisTemplateForList");
        }
    }

    @Test
    public void testRedisTemplateForSet() {
        try {
            redisTemplate.opsForSet().add("testRedisTemplateForSet", "yinyg");
            redisTemplate.opsForSet().add("testRedisTemplateForSet", "zhangsan");
            redisTemplate.opsForSet().add("testRedisTemplateForSet", "lisi");
            redisTemplate.opsForSet().add("testRedisTemplateForSet", "wangwu");
            Set<Object> set = redisTemplate.opsForSet().members("testRedisTemplateForSet");
            System.out.print("result:");
            if (!CollectionUtils.isEmpty(set)) {
                for (Object s : set) {
                    System.out.print(" " + s);
                }
            }
        } finally {
            redisTemplate.delete("testRedisTemplateForSet");
        }
    }

    @Test
    public void testRedisTemplateForZSet() {
        try {
            redisTemplate.opsForZSet().add("testRedisTemplateForZSet", "yinyg", 1);
            redisTemplate.opsForZSet().add("testRedisTemplateForZSet", "zhangsan", 2);
            redisTemplate.opsForZSet().add("testRedisTemplateForZSet", "lisi", 3);
            redisTemplate.opsForZSet().add("testRedisTemplateForZSet", "wangwu", 4);
//            Set<ZSetOperations.TypedTuple> typedTupleSet = new HashSet<>();
//            typedTupleSet.add(new DefaultTypedTuple("yinyg", 1d));
//            typedTupleSet.add(new DefaultTypedTuple("zhangsan", 2d));
//            typedTupleSet.add(new DefaultTypedTuple("lisi", 3d));
//            typedTupleSet.add(new DefaultTypedTuple("wangwu", 4d));
//            redisTemplate.opsForZSet().add("testRedisTemplateForZSet", typedTupleSet);
            Set<Object> set = redisTemplate.opsForZSet().range("testRedisTemplateForZSet", 0, -1);
            System.out.print("result:");
            if (!CollectionUtils.isEmpty(set)) {
                for (Object s : set) {
                    System.out.print(" " + s);
                }
            }
        } finally {
            redisTemplate.delete("testRedisTemplateForZSet");
        }
    }
}
