package com.demo.smartpark;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

    @Test
    public void generateEncodedPassword() {
        // 设置要加密的明文密码
        String rawPassword = "root"; // 可修改为需要的密码

        // 创建 BCryptPasswordEncoder 对象（也可以使用注入，但直接 new 更简单）
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 加密
        String encodedPassword = encoder.encode(rawPassword);

        // 输出到控制台
        System.out.println("明文密码: " + rawPassword);
        System.out.println("加密后密码: " + encodedPassword);
    }
}
