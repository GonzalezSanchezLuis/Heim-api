package com.heim.api.payment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
   private  String paymentURL;
   private String paymentMethod;
   private String paymentInfo;
   private BigDecimal amount;
   private String currency;
   private Long tripId;

}
