package com.heim.api.users.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPaymentRequest {
    private String firstName;
    private String email;
    private String phoneNumber;
    private String country;
    private String lastName;
    private String idNumber;

}
