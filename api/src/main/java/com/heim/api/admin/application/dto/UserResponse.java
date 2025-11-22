package com.heim.api.admin.application.dto;

import com.heim.api.drivers.domain.entity.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String  document;
    private String phone;
    private boolean active;
    private String role;

    private Driver driver;
}
