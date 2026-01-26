package com.heim.api.admin.infraestructure.controller;

import com.heim.api.payment.application.dto.PaymentConfirmationRequest;
import com.heim.api.payment.application.service.SettlementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RequestMapping("/api/v1/payments/admin/")
@RestController
public class PayoutAdminController {
    private final SettlementService settlementService;

    public PayoutAdminController(SettlementService settlementService){
        this.settlementService = settlementService;
    }

    @PostMapping("payout")
    public ResponseEntity<?> manualPayout(
            @RequestBody PaymentConfirmationRequest request) {

        try {
            settlementService.confirmPayout(
                    request.getDriverIds(),
                    request.getPaymentBatchReference()
            );

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());

        }



        return ResponseEntity.ok("Pagos realizados correctamente");
    }

}
