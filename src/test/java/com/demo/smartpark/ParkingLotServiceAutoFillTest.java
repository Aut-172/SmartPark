package com.demo.smartpark;

import com.demo.smartpark.parking.entity.ParkingLot;
import com.demo.smartpark.parking.service.IParkingLotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试 MyBatis-Plus 自动填充功能
 */
@SpringBootTest
@Transactional  // 测试完成后回滚，避免污染数据库
public class ParkingLotServiceAutoFillTest {

    @Autowired
    private IParkingLotService parkingLotService;

    @Test
    void testInsertAutoFill() {
        // 创建一个停车场对象，不设置时间字段
        ParkingLot lot = new ParkingLot();
        lot.setName("测试停车场");
        lot.setAddress("测试地址");
        lot.setTotalSpaces(100);
        lot.setAvailableSpaces(80);
        lot.setFeeRate(new BigDecimal("10.00"));

        // 执行插入
        boolean saved = parkingLotService.save(lot);
        assertThat(saved).isTrue();
        assertThat(lot.getId()).isNotNull();

        // 验证自动填充的时间字段非空
        assertThat(lot.getCreateTime()).isNotNull();
        assertThat(lot.getUpdateTime()).isNotNull();

        // 验证时间在合理范围内（当前时间前后几秒）
        LocalDateTime now = LocalDateTime.now();
        assertThat(lot.getCreateTime()).isBeforeOrEqualTo(now.plusSeconds(1));
        assertThat(lot.getCreateTime()).isAfterOrEqualTo(now.minusSeconds(5));
        // 插入时 createTime 和 updateTime 应相同
        assertThat(lot.getUpdateTime()).isEqualTo(lot.getCreateTime());
    }

    @Test
    void testUpdateAutoFill() throws InterruptedException {
        // 先插入一个停车场
        ParkingLot lot = new ParkingLot();
        lot.setName("更新测试");
        lot.setAddress("更新地址");
        lot.setTotalSpaces(50);
        lot.setAvailableSpaces(30);
        lot.setFeeRate(new BigDecimal("8.00"));
        parkingLotService.save(lot);

        // 记录插入后的时间
        LocalDateTime originalCreateTime = lot.getCreateTime();
        LocalDateTime originalUpdateTime = lot.getUpdateTime();

        System.out.println("插入后 createTime: " + originalCreateTime);
        System.out.println("插入后 updateTime: " + originalUpdateTime);

        // 等待一小段时间，确保更新时间变化
        Thread.sleep(2000); // 等待2秒，更明显
        System.out.println("休眠2s");

        // 修改部分字段
        lot.setName("更新测试-已修改");
        lot.setAvailableSpaces(25);
        boolean updated = parkingLotService.updateById(lot);
        assertThat(updated).isTrue();

        // 重新从数据库获取最新数据
        ParkingLot updatedLot = parkingLotService.getById(lot.getId());
        assertThat(updatedLot).isNotNull();

        System.out.println("更新后 createTime: " + updatedLot.getCreateTime());
        System.out.println("更新后 updateTime: " + updatedLot.getUpdateTime());
    }
}
