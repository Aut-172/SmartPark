package com.demo.smartpark.lot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.smartpark.lot.dto.ParkingLotCreateRequest;
import com.demo.smartpark.lot.entity.ParkingLot;
import com.demo.smartpark.lot.mapper.ParkingLotMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public ParkingLot createParkingLot(ParkingLotCreateRequest request) {
        // 校验剩余车位不能大于总车位
        if (request.getAvailableSpaces() > request.getTotalSpaces()) {
            throw new RuntimeException("剩余车位数不能大于总车位数");
        }

        ParkingLot lot = new ParkingLot();
        lot.setName(request.getName());
        lot.setAddress(request.getAddress());
        lot.setTotalSpaces(request.getTotalSpaces());
        lot.setAvailableSpaces(request.getAvailableSpaces());
        lot.setLatitude(request.getLatitude());
        lot.setLongitude(request.getLongitude());
        lot.setFeeRate(request.getFeeRate());
        // version 默认为0

        save(lot);
        return lot;
    }

    @Override
    @Transactional
    public ParkingLot updateParkingLot(Long id, ParkingLotCreateRequest request) {
        ParkingLot lot = getById(id);
        if (lot == null) {
            throw new RuntimeException("停车场不存在");
        }

        // 校验剩余车位不能大于总车位
        if (request.getAvailableSpaces() > request.getTotalSpaces()) {
            throw new RuntimeException("剩余车位数不能大于总车位数");
        }

        lot.setName(request.getName());
        lot.setAddress(request.getAddress());
        lot.setTotalSpaces(request.getTotalSpaces());
        lot.setAvailableSpaces(request.getAvailableSpaces());
        lot.setLatitude(request.getLatitude());
        lot.setLongitude(request.getLongitude());
        lot.setFeeRate(request.getFeeRate());

        updateById(lot);  // 乐观锁自动处理 version
        return lot;
    }

    @Override
    @Transactional
    public ParkingLot increaseAvailableSpaces(Long id, int increment) {
        ParkingLot lot = getById(id);
        if (lot == null) {
            throw new RuntimeException("停车场不存在");
        }

        int newSpaces = lot.getAvailableSpaces() + increment;
        // 不能超过总车位
        if (newSpaces > lot.getTotalSpaces()) {
            throw new RuntimeException("增加后剩余车位数不能超过总车位数");
        }

        lot.setAvailableSpaces(newSpaces);
        updateById(lot);  // 乐观锁自动处理 version
        return lot;
    }
}
