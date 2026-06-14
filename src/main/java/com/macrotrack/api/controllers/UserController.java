package com.macrotrack.api.controllers;

import com.macrotrack.api.dto.UpdateUserRequest;
import com.macrotrack.api.entity.User;
import com.macrotrack.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {
        User user = userRepository.findById(UUID.fromString(auth.getName())).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(Map.of(
            "userId", user.getId().toString(),
            "email", user.getEmail(),
            "name", user.getName(),
            "dailyCalorieGoal", user.getDailyCalorieGoal(),
            "proteinRatio", user.getProteinRatio(),
            "fatRatio", user.getFatRatio(),
            "carbsRatio", user.getCarbsRatio()
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication auth, @RequestBody UpdateUserRequest request) {
        User user = userRepository.findById(UUID.fromString(auth.getName())).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        if (request.getName() != null) user.setName(request.getName());
        if (request.getDailyCalorieGoal() != null) user.setDailyCalorieGoal(request.getDailyCalorieGoal());
        if (request.getProteinRatio() != null) user.setProteinRatio(request.getProteinRatio());
        if (request.getFatRatio() != null) user.setFatRatio(request.getFatRatio());
        if (request.getCarbsRatio() != null) user.setCarbsRatio(request.getCarbsRatio());

        userRepository.save(user);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "name", user.getName(),
            "dailyCalorieGoal", user.getDailyCalorieGoal(),
            "proteinRatio", user.getProteinRatio(),
            "fatRatio", user.getFatRatio(),
            "carbsRatio", user.getCarbsRatio()
        ));
    }
}
