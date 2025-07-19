package com.heim.api.payment.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotBlank(message = "La moneda es obligatoria")
    private String currency;

    @NotBlank(message = "El número de factura es obligatorio")
    private String invoice;

    @NotBlank(message = "El monto es obligatorio")
    private String amount;

    @Email(message = "El correo no es válido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    private String method; // puede ser null si es opcional
}
