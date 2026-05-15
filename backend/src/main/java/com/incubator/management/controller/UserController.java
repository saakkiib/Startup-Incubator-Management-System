package com.incubator.management.controller;

import com.incubator.management.dto.UserResponse;
import com.incubator.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable Long id, @RequestBody UserResponse request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }
}
