package com.heim.api.payment.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.heim.api.users.application.dto.UserPaymentRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @JsonProperty("redirect_link")
    private String redirectLink;

    @NotBlank(message = "El monto es obligatorio")
    private BigDecimal amount;

    @JsonProperty("user")
    private UserPaymentRequest userPaymentRequest;

    @JsonProperty("order_key")
    private String  orderKey;

}
