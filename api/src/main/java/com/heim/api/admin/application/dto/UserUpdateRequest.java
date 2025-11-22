package com.heim.api.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private Long userId;
    private String fullName;
    private String email;
    private String  document;
    private String phone;
    private boolean active;

    private DriverUpdateRequestDTO driver;
}
