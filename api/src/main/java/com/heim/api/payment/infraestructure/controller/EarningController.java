package com.heim.api.payment.infraestructure.controller;

import com.heim.api.payment.application.dto.EarningsDTO;
import com.heim.api.payment.application.service.EarningService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin("*")
public class EarningController {
    private  final EarningService earningService;

    EarningController(EarningService earningService){
        this.earningService = earningService;
    }

    @GetMapping("/{driverId}/earning")
    public EarningsDTO getDriverEarnings(@PathVariable  Long driverId){
        return earningService.getDriverEarnings(driverId);
    }

}
