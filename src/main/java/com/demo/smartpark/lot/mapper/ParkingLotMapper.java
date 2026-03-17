package com.demo.smartpark.lot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.smartpark.lot.entity.ParkingLot;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParkingLotMapper extends BaseMapper<ParkingLot> {
}
