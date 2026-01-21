package com.heim.api.payment.application.service;

import com.heim.api.payment.application.dto.DriverPaymentAccountRequest;
import com.heim.api.payment.domain.DriverPaymentAccount;
import com.heim.api.payment.infraestructure.repository.DriverPaymentAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class DriverPaymentAccountService {
    private final DriverPaymentAccountRepository driverPaymentAccountRepository;

    @Autowired
    public DriverPaymentAccountService(DriverPaymentAccountRepository driverPaymentAccountRepository){
        this.driverPaymentAccountRepository = driverPaymentAccountRepository;
    }

    @Transactional
    public DriverPaymentAccount saveOrUpdateAccount(DriverPaymentAccountRequest request){
        Optional<DriverPaymentAccount> existingAccountOpt = driverPaymentAccountRepository.findByDriverId(request.getDriverId());

        DriverPaymentAccount accountToSave;

        if (existingAccountOpt.isPresent()){
            accountToSave = existingAccountOpt.get();

            accountToSave.setPaymentMethod(request.getPaymentMethod().toUpperCase());
            accountToSave.setAccountNumber(request.getAccountNumber());

        }else {
            accountToSave = new DriverPaymentAccount();

            accountToSave.setDriverId(request.getDriverId());
            accountToSave.setPaymentMethod(request.getPaymentMethod().toUpperCase());
            accountToSave.setAccountNumber(request.getAccountNumber());
            accountToSave.setPrimary(true);

        }
      return   driverPaymentAccountRepository.save(accountToSave);
    }

    public DriverPaymentAccount getAccountByDriverId(Long driverId) {
        return driverPaymentAccountRepository.findByDriverId(driverId).orElse(null);
    }
}
