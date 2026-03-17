package com.demo.smartpark.car.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CarAddRequest {
    @NotBlank(message = "车牌号不能为空")
    private String plateNumber;
}