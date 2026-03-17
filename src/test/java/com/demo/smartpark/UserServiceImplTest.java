package com.demo.smartpark;

import com.demo.smartpark.user.entity.User;
import com.demo.smartpark.user.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * UserService 的集成测试
 * 使用 @Transactional 保证测试数据不会真正持久化到数据库
 */
@SpringBootTest
@Transactional
public class UserServiceImplTest {

    @Autowired
    private IUserService userService;

    @Test
    void testSaveAndGet() {
        // 创建用户对象
        User user = new User();
        user.setPhone("13800138000");
        user.setPassword("encodedPassword"); // 假设是加密后的密码
        user.setNickname("测试用户");
        user.setRole(0);
        user.setPoints(100);

        // 执行保存
        boolean saveResult = userService.save(user);
        assertThat(saveResult).isTrue();
        assertThat(user.getId()).isNotNull(); // 主键自动回填

        // 根据ID查询
        User found = userService.getById(user.getId());
        assertThat(found).isNotNull();
        assertThat(found.getPhone()).isEqualTo("13800138000");
        assertThat(found.getNickname()).isEqualTo("测试用户");
        assertThat(found.getRole()).isZero();
        assertThat(found.getPoints()).isEqualTo(100);
    }

    @Test
    void testUpdate() {
        // 先保存一个用户
        User user = new User();
        user.setPhone("13800138001");
        user.setPassword("encodedPassword");
        user.setNickname("旧昵称");
        userService.save(user);
        Long id = user.getId();

        // 修改昵称
        user.setNickname("新昵称");
        boolean updateResult = userService.updateById(user);
        assertThat(updateResult).isTrue();

        // 验证修改成功
        User updated = userService.getById(id);
        assertThat(updated.getNickname()).isEqualTo("新昵称");
    }

    @Test
    void testDelete() {
        // 先保存一个用户
        User user = new User();
        user.setPhone("13800138002");
        user.setPassword("encodedPassword");
        userService.save(user);
        Long id = user.getId();

        // 执行删除
        boolean deleteResult = userService.removeById(id);
        assertThat(deleteResult).isTrue();

        // 验证删除成功
        User deleted = userService.getById(id);
        assertThat(deleted).isNull();
    }
}

