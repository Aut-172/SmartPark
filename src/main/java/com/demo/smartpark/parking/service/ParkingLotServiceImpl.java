package com.demo.smartpark.parking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.smartpark.parking.entity.ParkingLot;
import com.demo.smartpark.parking.mapper.ParkingLotMapper;
import org.springframework.stereotype.Service;

@Service
public class ParkingLotServiceImpl extends ServiceImpl<ParkingLotMapper,ParkingLot> implements IParkingLotService{
    @Override
    public IPage<ParkingLot> getParkingLotPage(int pageNum, int pageSize) {
        // 创建分页对象
        Page<ParkingLot> page = new Page<>(pageNum, pageSize);
        // 使用 lambdaQuery 进行分页查询，按创建时间倒序排列
        return lambdaQuery()
                .orderByDesc(ParkingLot::getCreateTime)
                .page(page);
    }

    @Override
    public ParkingLot getParkingLotDetail(Long id) {
        // 直接调用 MyBatis-Plus 的 getById 方法
        return getById(id);
    }
}
