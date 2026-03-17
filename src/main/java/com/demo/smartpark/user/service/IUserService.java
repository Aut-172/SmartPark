package com.demo.smartpark.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.smartpark.auth.dto.RegisterRequest;
import com.demo.smartpark.user.entity.User;

public interface IUserService extends IService<User> {
    User getByPhone(String phone);

    User register(RegisterRequest registerRequest);
}
