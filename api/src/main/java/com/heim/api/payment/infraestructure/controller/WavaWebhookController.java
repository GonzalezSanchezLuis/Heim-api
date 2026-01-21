package com.heim.api.payment.infraestructure.controller;

import com.heim.api.payment.application.dto.WavaWebhookRequest;
import com.heim.api.payment.application.service.WavaWebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wava")
@CrossOrigin("*")
public class WavaWebhookController {
    private final WavaWebhookService wavaWebhookService;

    public WavaWebhookController(WavaWebhookService wavaWebhookService) {
        this.wavaWebhookService = wavaWebhookService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody WavaWebhookRequest request) {
        try {
            wavaWebhookService.processWebhook(request);
            return ResponseEntity.ok("received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }

}
