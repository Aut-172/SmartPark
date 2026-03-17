package com.demo.smartpark.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.smartpark.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
