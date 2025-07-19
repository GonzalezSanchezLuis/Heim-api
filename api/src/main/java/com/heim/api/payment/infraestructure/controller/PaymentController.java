package com.heim.api.payment.infraestructure.controller;

import com.heim.api.payment.application.dto.PaymentRequest;
import com.heim.api.payment.application.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/")
@CrossOrigin("*")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
   public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;

    }

   /* @PostMapping("create")
    public ResponseEntity<PayResponse> createPayment(@RequestBody PayRequest payRequest){
       String paymentUrl = paymentService.generatePayment(payRequest);
        return ResponseEntity.ok(new PayResponse(paymentUrl));

    }*/

    @PostMapping("generate")
    public ResponseEntity<?> generatePayment(@Valid @RequestBody PaymentRequest request) {
        String checkoutUrl = paymentService.generateCheckoutUrl(request);
        return ResponseEntity.ok(Map.of("checkoutUrl", checkoutUrl));
    }

}
