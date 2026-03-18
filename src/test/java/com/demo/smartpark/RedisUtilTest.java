package com.demo.smartpark;
import com.demo.smartpark.common.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RedisUtil 工具类测试
 * 需确保Redis服务已启动（默认localhost:6379）
 */
@SpringBootTest
public class RedisUtilTest {

    @Autowired
    private RedisUtil redisUtil;

    // 测试用的简单实体类（需与之前RedisConfig中序列化配置兼容）
    static class User {
        private Long id;
        private String name;
        private String email;

        // 无参构造（必须）
        public User() {}

        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // getter/setter（必须）
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        // 为了比较，可以重写equals/hashCode，但这里简单比较字段
    }

    @Test
    public void testSetGetString() {
        String key = "test:util:string";
        String value = "hello redis util";

        // set
        boolean result = redisUtil.set(key, value);
        assertThat(result).isTrue();

        // get
        Object obj = redisUtil.get(key);
        assertThat(obj).isEqualTo(value);

        // 带类型的get
        String str = redisUtil.get(key, String.class);
        assertThat(str).isEqualTo(value);

        // 清理
        redisUtil.del(key);
    }

    @Test
    public void testSetGetObject() {
        String key = "test:util:user";
        User user = new User(1L, "张三", "zhangsan@example.com");

        // set
        boolean result = redisUtil.set(key, user);
        assertThat(result).isTrue();

        // get (需强制转换)
        Object obj = redisUtil.get(key);
        assertThat(obj).isInstanceOf(User.class);
        User retrieved = (User) obj;
        assertThat(retrieved.getId()).isEqualTo(user.getId());
        assertThat(retrieved.getName()).isEqualTo(user.getName());

        // 带类型的get
        User user2 = redisUtil.get(key, User.class);
        assertThat(user2).isNotNull();
        assertThat(user2.getId()).isEqualTo(user.getId());

        // 清理
        redisUtil.del(key);
    }

    @Test
    public void testExpire() {
        String key = "test:util:expire";
        String value = "temp";

        redisUtil.set(key, value);
        // 设置过期时间为2秒
        boolean expireResult = redisUtil.expire(key, 2);
        assertThat(expireResult).isTrue();

        // 立即获取剩余时间，应大于0
        long ttl = redisUtil.getExpire(key);
        assertThat(ttl).isGreaterThan(0);

        // 等待3秒后，key应自动删除
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        boolean exists = redisUtil.hasKey(key);
        assertThat(exists).isFalse();

        // 无需手动删除
    }

    @Test
    public void testIncrDecr() {
        String key = "test:util:counter";

        // 初始化
        redisUtil.set(key, 10);
        // 递增5
        long newValue = redisUtil.incr(key, 5);
        assertThat(newValue).isEqualTo(15);

        // 递减3
        long decrValue = redisUtil.decr(key, 3);
        assertThat(decrValue).isEqualTo(12);

        // 直接获取
        Integer val = redisUtil.get(key, Integer.class);
        assertThat(val).isEqualTo(12);

        // 清理
        redisUtil.del(key);
    }

    @Test
    public void testHashOperations() {
        String key = "test:util:hash";
        String field1 = "name";
        String field2 = "age";
        String field3 = "user";

        // hset
        boolean hset1 = redisUtil.hset(key, field1, "测试停车场");
        assertThat(hset1).isTrue();

        boolean hset2 = redisUtil.hset(key, field2, 5);
        assertThat(hset2).isTrue();

        User user = new User(2L, "李四", "lisi@example.com");
        boolean hset3 = redisUtil.hset(key, field3, user);
        assertThat(hset3).isTrue();

        // hget
        Object nameObj = redisUtil.hget(key, field1);
        assertThat(nameObj).isEqualTo("测试停车场");

        Integer age = redisUtil.hget(key, field2, Integer.class);
        assertThat(age).isEqualTo(5);

        User retrievedUser = redisUtil.hget(key, field3, User.class);
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getName()).isEqualTo("李四");

        // hdel
        redisUtil.hdel(key, field1);
        boolean exists = redisUtil.hHasKey(key, field1);
        assertThat(exists).isFalse();

        // 清理整个key
        redisUtil.del(key);
    }

    @Test
    public void testHashIncrDecr() {
        String key = "test:util:hashcounter";
        String field = "count";

        // 初始不存在，hincr 应创建并返回增加后的值
        double result = redisUtil.hincr(key, field, 5);
        assertThat(result).isEqualTo(5.0);

        // 再增加3
        result = redisUtil.hincr(key, field, 3);
        assertThat(result).isEqualTo(8.0);

        // 递减2
        result = redisUtil.hdecr(key, field, 2);
        assertThat(result).isEqualTo(6.0);

        // 直接获取
        Integer val = redisUtil.hget(key, field, Integer.class);
        assertThat(val).isEqualTo(6);

        redisUtil.del(key);
    }
}