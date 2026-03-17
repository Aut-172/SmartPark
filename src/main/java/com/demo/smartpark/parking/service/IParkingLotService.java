package com.demo.smartpark.parking.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.smartpark.parking.entity.ParkingLot;

public interface IParkingLotService extends IService<ParkingLot> {
    /**
     * 分页查询停车场列表
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页数据
     */
    IPage<ParkingLot> getParkingLotPage(int pageNum, int pageSize);

    /**
     * 根据ID查询停车场详情
     * @param id 停车场ID
     * @return 停车场实体，不存在返回null
     */
    ParkingLot getParkingLotDetail(Long id);
}
