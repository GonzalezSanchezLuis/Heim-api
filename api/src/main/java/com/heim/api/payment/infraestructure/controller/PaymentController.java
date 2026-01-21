package com.heim.api.payment.infraestructure.controller;

import com.heim.api.payment.application.dto.PaymentConfirmationRequest;
import com.heim.api.payment.application.dto.PaymentRequest;
import com.heim.api.payment.application.service.PaymentService;
import com.heim.api.payment.application.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin("*")
public class PaymentController {
    private final PaymentService paymentService;
    private final SettlementService settlementService;

    @Autowired
   public PaymentController(PaymentService paymentService, SettlementService settlementService){
        this.paymentService = paymentService;
        this.settlementService = settlementService;


    }

    @PostMapping("/create")
    public String createPayment(@RequestBody PaymentRequest request) throws Exception {
        return paymentService.createPaymentLink(request);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayout(@RequestBody PaymentConfirmationRequest request) {
        if (request.getDriverIds() == null || request.getDriverIds().isEmpty()) {
            return ResponseEntity.badRequest().body("La lista de driverIds no puede estar vacía.");
        }

        // Asumiendo que el campo paymentBatchReference está en el DTO
        String batchRef = request.getPaymentBatchReference() != null ? request.getPaymentBatchReference() : "MANUAL_PAYOUT_" + System.currentTimeMillis();

        try {
            settlementService.confirmPayout(request.getDriverIds(), batchRef);
            return ResponseEntity.ok("Pago confirmado y saldos actualizados para " + request.getDriverIds().size() + " conductores.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al confirmar el pago: " + e.getMessage());
        }
    }


}
