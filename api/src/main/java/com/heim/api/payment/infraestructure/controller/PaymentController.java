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
    

    @PostMapping("/create")
    public String createPayment(@RequestBody PaymentRequest request) throws Exception {
        return paymentService.createPaymentLink(request);
    }

}
