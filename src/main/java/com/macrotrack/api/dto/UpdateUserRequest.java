package com.macrotrack.api.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private Integer dailyCalorieGoal;
    private Double proteinRatio;
    private Double fatRatio;
    private Double carbsRatio;
}
