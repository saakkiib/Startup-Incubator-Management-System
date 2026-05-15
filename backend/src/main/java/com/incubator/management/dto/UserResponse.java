package com.incubator.management.dto;

import com.incubator.management.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private User.Role role;
    private String fullName;
    private String phone;
    private boolean isActive;
}
