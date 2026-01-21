package com.heim.api.payment.infraestructure.controller;

import com.heim.api.payment.application.service.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/test/")
public class TestPaymentController {
    private final SettlementService settlementService;

    public TestPaymentController(SettlementService settlementService){
        this.settlementService = settlementService;
    }

    @PostMapping("force-settlement")
    public ResponseEntity<String> forceSettlement(){
        try {
           settlementService.processWeeklySettlement();
           return  ResponseEntity.ok("Proceso de liquidación ejecutado manualmente con éxito.");
        } catch ( Exception e){
            return ResponseEntity.internalServerError().body("Error al ejecutar la liquidación: " + e.getMessage());
        }
    }

}
