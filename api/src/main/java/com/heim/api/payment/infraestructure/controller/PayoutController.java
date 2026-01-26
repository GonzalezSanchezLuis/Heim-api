package com.heim.api.payment.infraestructure.controller;

import com.heim.api.payment.domain.Payout;
import com.heim.api.payment.infraestructure.repository.PayoutRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/payments/")
public class PayoutController {
    private final PayoutRepository payoutRepository;

    public PayoutController(PayoutRepository payoutRepository) {
        this.payoutRepository = payoutRepository;
    }

    @GetMapping("/drivers/{driverId}/payouts")
    public List<Payout> getPayoutHistory(@PathVariable Long driverId) {
        return payoutRepository.findByDriverId(driverId);
    }
}
