package com.heim.api.admin.infraestructure.controller;

import com.heim.api.admin.application.dto.AdminDriverPendingDTO;
import com.heim.api.payment.application.service.DriverPaymentAccountService;
import com.heim.api.payment.domain.DriverBalance;
import com.heim.api.payment.domain.DriverPaymentAccount;
import com.heim.api.payment.infraestructure.repository.DriverBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/payments/admin/")
@CrossOrigin("*")
public class AdminDriverController {
    private final DriverBalanceRepository driverBalanceRepository;
    private final DriverPaymentAccountService driverPaymentAccountService;

    @Autowired
    public  AdminDriverController(   DriverBalanceRepository driverBalanceRepository,
                                     DriverPaymentAccountService driverPaymentAccountService){
        this.driverBalanceRepository = driverBalanceRepository;
        this.driverPaymentAccountService = driverPaymentAccountService;
    }
    @GetMapping("/pending-drivers")
    public ResponseEntity<List<AdminDriverPendingDTO>> getDriversWithPendingBalance() {
        List<AdminDriverPendingDTO> result = new ArrayList<>();

        for (DriverBalance balance : driverBalanceRepository.findAll()) {
            if (balance.getAvailableBalance().compareTo(BigDecimal.ZERO) > 0) {
                DriverPaymentAccount account = driverPaymentAccountService.getAccountByDriverId(balance.getDriverId());
                String paymentMethod = account != null ? account.getPaymentMethod() : null;
                String accountNumber = account != null ? account.getAccountNumber() : null;

                result.add(new AdminDriverPendingDTO(
                        balance.getDriverId(),
                        balance.getAvailableBalance(),
                        paymentMethod,
                        accountNumber
                ));
            }
        }

        return ResponseEntity.ok(result);
    }



}
