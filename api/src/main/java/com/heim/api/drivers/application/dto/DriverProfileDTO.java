package com.heim.api.drivers.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileDTO {
    private String name;
    private String phone;
    private String urlAvatar;
   // private double rating;
   // private int tripCount;
  //  private List<String> securityChecks;
}
