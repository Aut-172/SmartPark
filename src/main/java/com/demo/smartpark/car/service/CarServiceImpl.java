package com.demo.smartpark.car.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.smartpark.car.entity.Car;
import com.demo.smartpark.car.mapper.CarMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, Car> implements ICarService {
    @Override
    public List<Car> getUserCars(Long userId) {
        return lambdaQuery().eq(Car::getUserId, userId)
                .orderByDesc(Car::getIsDefault) // 默认车牌排前面
                .orderByDesc(Car::getCreateTime)
                .list();
    }

    @Override
    @Transactional
    public Car addCar(Long userId, String plateNumber) {
        // 检查车牌是否已存在（全局唯一）
        boolean exists = lambdaQuery().eq(Car::getPlateNumber, plateNumber).exists();
        if (exists) {
            throw new RuntimeException("车牌号已被绑定");
        }

        Car car = new Car();
        car.setUserId(userId);
        car.setPlateNumber(plateNumber);
        car.setIsDefault(0); // 新增时不设为默认
        save(car);
        return car;
    }

    @Override
    @Transactional
    public void deleteCar(Long userId, Long carId) {
        Car car = getById(carId);
        if (car == null || !car.getUserId().equals(userId)) {
            throw new RuntimeException("车牌不存在或无权限删除");
        }
        removeById(carId);
        // 如果删除的是默认车牌，无需额外操作，后续设置默认即可
    }

    @Override
    @Transactional
    public void setDefaultCar(Long userId, Long carId) {
        Car car = getById(carId);
        if (car == null || !car.getUserId().equals(userId)) {
            throw new RuntimeException("车牌不存在或无权限操作");
        }

        // 先将该用户所有车牌设为非默认
        lambdaUpdate()
                .eq(Car::getUserId, userId)
                .set(Car::getIsDefault, 0)
                .update();

        // 再将指定车牌设为默认
        lambdaUpdate()
                .eq(Car::getId, carId)
                .set(Car::getIsDefault, 1)
                .update();
    }

    @Override
    public Car getDefaultCar(Long userId) {
        return lambdaQuery()
                .eq(Car::getUserId, userId)
                .eq(Car::getIsDefault, 1)
                .one();
    }
}
