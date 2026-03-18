package com.demo.smartpark;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring Data Redis 集成测试
 * 需确保Redis服务已启动（默认localhost:6379）
 */
@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate; // 专门操作字符串的模板，key和value都是String

    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // 通用模板，可序列化对象

    @Test
    public void testStringOperations() {
        // 获取操作字符串的结构
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        String key = "test:simple:key";
        String value = "hello redis";

        // 写入
        ops.set(key, value);

        // 读取
        String result = ops.get(key);
        assertThat(result).isEqualTo(value);

        // 设置过期时间（10秒）
        stringRedisTemplate.expire(key, 10, TimeUnit.SECONDS);
        Long ttl = stringRedisTemplate.getExpire(key);
        assertThat(ttl).isGreaterThan(0);

        // 删除
        Boolean deleted = stringRedisTemplate.delete(key);
        assertThat(deleted).isTrue();
    }

    @Test
    public void testObjectOperations() {
        // 对于对象类型，需确保序列化方式一致（默认JDK序列化，建议使用Jackson2JsonRedisSerializer）
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        String key = "test:object:user";
        User user = new User(1L, "张三", "zhangsan@example.com");

        // 写入对象
        ops.set(key, user);

        // 读取对象
        User result = (User) ops.get(key);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());

        // 删除
        redisTemplate.delete(key);
    }

    @Test
    public void testHashOperations() {
        String key = "test:hash:parking";
        String field1 = "name";
        String field2 = "totalSpaces";

        // 操作hash
        redisTemplate.opsForHash().put(key, field1, "中央停车场");
        redisTemplate.opsForHash().put(key, field2, 200);

        // 读取
        String name = (String) redisTemplate.opsForHash().get(key, field1);
        Integer total = (Integer) redisTemplate.opsForHash().get(key, field2);

        assertThat(name).isEqualTo("中央停车场");
        assertThat(total).isEqualTo(200);

        // 删除整个key
        redisTemplate.delete(key);
    }

    @Test
    public void testAtomicIncrement() {
        String key = "test:counter:availableSpots";

        // 初始化
        stringRedisTemplate.opsForValue().set(key, "100");

        // 原子递增（扣减一个车位）
        Long newValue = stringRedisTemplate.opsForValue().increment(key, -1);
        assertThat(newValue).isEqualTo(99);

        // 读取当前值
        String current = stringRedisTemplate.opsForValue().get(key);
        assertThat(current).isEqualTo("99");

        stringRedisTemplate.delete(key);
    }

    /**
     * 简单的User类（需实现Serializable或配置合适的序列化器）
     */
    static class User {
        private Long id;
        private String name;
        private String email;

        // 无参构造、全参构造、getter/setter（略，使用Lombok可简化）
        public User() {}

        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // getter/setter 省略，实际需生成
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}