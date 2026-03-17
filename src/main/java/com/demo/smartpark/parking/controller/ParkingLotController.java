package com.demo.smartpark.parking.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.demo.smartpark.common.Response;
import com.demo.smartpark.parking.entity.ParkingLot;
import com.demo.smartpark.parking.service.IParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parkinglots")
public class ParkingLotController {
    @Autowired
    private IParkingLotService parkingLotService;

    /**
     * 分页查询停车场列表
     * @param pageNum  页码，默认1
     * @param pageSize 每页条数，默认10
     * @return 分页结果
     */
    @GetMapping
    public Response<IPage<ParkingLot>> listParkingLots(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<ParkingLot> page = parkingLotService.getParkingLotPage(pageNum, pageSize);
        return Response.success(page);
    }

    /**
     * 获取停车场详情
     * @param id 停车场ID
     * @return 停车场信息
     */
    @GetMapping("/{id}")
    public Response<ParkingLot> getParkingLotDetail(@PathVariable Long id) {
        ParkingLot parkingLot = parkingLotService.getParkingLotDetail(id);
        if (parkingLot == null) {
            return Response.error("停车场不存在");
        }
        return Response.success(parkingLot);
    }
}
