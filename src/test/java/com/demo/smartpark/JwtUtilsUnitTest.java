package com.demo.smartpark;

import com.demo.smartpark.common.JsonUtils;
import com.demo.smartpark.common.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JwtUtils 的单元测试（需要 Spring 上下文加载配置）
 */

import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JwtUtils 的纯单元测试（无需启动 Spring 上下文）
 */
class JwtUtilsUnitTest {

    @BeforeAll
    static void setup() throws Exception {
        // 准备测试用的密钥（Base64 编码，至少 32 字节）
        String testKey = Base64.getEncoder().encodeToString("my-test-secret-key-for-jwt-hs256".getBytes());
        Long testTtl = 3600000L;          // 1 小时（毫秒）
        String testIssuer = "TestIssuer";

        // 通过反射设置 JwtUtils 的私有静态字段
        setStaticField(JwtUtils.class, "jwtKey", testKey);
        setStaticField(JwtUtils.class, "jwtTtl", testTtl);
        setStaticField(JwtUtils.class, "jwtIssuer", testIssuer);
    }

    private static void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    @Test
    @DisplayName("生成并解析 JWT，验证 claims 内容")
    void testCreateAndParseJwt() throws Exception {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", 1L);
        claimsMap.put("phone", "13800138000");
        claimsMap.put("role", 0);
        String subject = JsonUtils.toJson(claimsMap); // 假设 JsonUtils 可用

        String token = JwtUtils.createJWT(subject);
        assertThat(token).isNotNull();

        Claims claims = JwtUtils.parseJWT(token);
        assertThat(claims.getSubject()).isEqualTo(subject);

        // 可选：验证 subject 中的具体字段
        String json = claims.getSubject();
        Map<String, Object> parsedMap = JsonUtils.fromJson(json, Map.class);
        assertThat(parsedMap)
                .containsEntry("id", 1)
                .containsEntry("phone", "13800138000")
                .containsEntry("role", 0);
    }

    @Test
    @DisplayName("篡改签名应抛出 SignatureException")
    void testInvalidSignature() {
        String subject = "test";
        String token = JwtUtils.createJWT(subject);
        String[] parts = token.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + ".badsignature";

        assertThatThrownBy(() -> JwtUtils.parseJWT(tamperedToken))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("过期的 Token 应抛出 ExpiredJwtException")
    void testExpiredToken() throws Exception {
        String subject = "expired";
        long shortTtl = 1L; // 1 毫秒
        String token = JwtUtils.createJWT(subject, shortTtl);
        Thread.sleep(10); // 确保过期

        assertThatThrownBy(() -> JwtUtils.parseJWT(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("畸形 Token 应抛出异常")
    void testMalformedToken() {
        String malformed = "not.a.valid.token";
        assertThatThrownBy(() -> JwtUtils.parseJWT(malformed))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("签发者应与设置一致")
    void testIssuer() throws Exception {
        String subject = "test";
        String token = JwtUtils.createJWT(subject);
        Claims claims = JwtUtils.parseJWT(token);
        assertThat(claims.getIssuer()).isEqualTo("TestIssuer");
    }
}
