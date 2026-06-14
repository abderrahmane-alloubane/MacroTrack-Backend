package com.macrotrack.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    @Column(name = "daily_calorie_goal")
    private Integer dailyCalorieGoal = 2000;

    @Column(name = "protein_ratio")
    private Double proteinRatio = 30.0;

    @Column(name = "fat_ratio")
    private Double fatRatio = 30.0;

    @Column(name = "carbs_ratio")
    private Double carbsRatio = 40.0;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
