package com.heim.api.payment.infraestructure.controller;

import com.heim.api.payment.application.dto.PayRequest;
import com.heim.api.payment.application.dto.PayResponse;
import com.heim.api.payment.application.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pay/")
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
   public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @PostMapping("create")
    public ResponseEntity<PayResponse> createPayment(@RequestBody PayRequest payRequest){
        String paymentUrl = paymentService.generatePayment(payRequest);
        return ResponseEntity.ok(new PayResponse(paymentUrl));

    }
}
