package com.demo.smartpark.car.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.smartpark.car.entity.Car;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CarMapper extends BaseMapper<Car> {
}
