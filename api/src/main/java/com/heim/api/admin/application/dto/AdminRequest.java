package com.heim.api.admin.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequest {
    private  Long adminId;
    private String name;
    private String email;
    private String password;
}
