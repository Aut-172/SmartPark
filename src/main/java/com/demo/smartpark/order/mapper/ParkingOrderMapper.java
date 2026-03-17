package com.demo.smartpark.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.smartpark.order.entity.ParkingOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParkingOrderMapper extends BaseMapper<ParkingOrder> {
}
