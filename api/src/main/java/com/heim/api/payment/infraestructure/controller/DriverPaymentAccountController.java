package com.heim.api.payment.infraestructure.controller;

import com.heim.api.payment.application.dto.DriverPaymentAccountResponse;
import com.heim.api.payment.application.dto.DriverPaymentAccountRequest;
import com.heim.api.payment.application.service.DriverPaymentAccountService;
import com.heim.api.payment.domain.DriverPaymentAccount;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/payment/")
@RestController
@CrossOrigin("*")
public class DriverPaymentAccountController {

    private final DriverPaymentAccountService  driverPaymentAccountService;

    public DriverPaymentAccountController(DriverPaymentAccountService driverPaymentAccountService){
        this.driverPaymentAccountService  = driverPaymentAccountService;
    }

    @PostMapping("driver/account")
    public ResponseEntity<DriverPaymentAccountResponse> savePaymentAccount(@RequestBody DriverPaymentAccountRequest request) {

        DriverPaymentAccount savedEntity = driverPaymentAccountService.saveOrUpdateAccount(request);

        DriverPaymentAccountResponse response = new DriverPaymentAccountResponse(
                savedEntity.getDriverId(),
                savedEntity.getPaymentMethod(),
                savedEntity.getAccountNumber()
        );
        return  ResponseEntity.ok(response);
    }

    // Controller
    @GetMapping("{driverId}/getAccount")
    public ResponseEntity<DriverPaymentAccountResponse> getPaymentAccount(@PathVariable Long driverId) {
        DriverPaymentAccount account = driverPaymentAccountService.getAccountByDriverId(driverId);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        DriverPaymentAccountResponse response = new DriverPaymentAccountResponse(
                account.getDriverId(),
                account.getPaymentMethod(),
                account.getAccountNumber()
        );


        return ResponseEntity.ok(response);
    }
}
