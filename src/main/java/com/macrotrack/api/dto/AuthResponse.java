package com.macrotrack.api.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String userId;
    private String email;
    private String name;
    private Integer dailyCalorieGoal;
    private Double proteinRatio;
    private Double fatRatio;
    private Double carbsRatio;

    public AuthResponse(String token, String userId, String email, String name, Integer dailyCalorieGoal,
                        Double proteinRatio, Double fatRatio, Double carbsRatio) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.dailyCalorieGoal = dailyCalorieGoal;
        this.proteinRatio = proteinRatio;
        this.fatRatio = fatRatio;
        this.carbsRatio = carbsRatio;
    }
}
