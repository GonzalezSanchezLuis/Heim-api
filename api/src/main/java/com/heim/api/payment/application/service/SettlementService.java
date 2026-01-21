package com.heim.api.payment.application.service;

import com.heim.api.payment.domain.DriverBalance;
import com.heim.api.payment.domain.Earning;
import com.heim.api.payment.infraestructure.repository.DriverBalanceRepository;
import com.heim.api.payment.infraestructure.repository.EarningRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SettlementService {
    private final EarningRepository earningRepository;
    private final DriverBalanceRepository driverBalanceRepository;

    public SettlementService(EarningRepository earningRepository,
                             DriverBalanceRepository driverBalanceRepository){
        this.earningRepository = earningRepository;
        this.driverBalanceRepository = driverBalanceRepository;

    }
    @Scheduled(cron = "0 0 12 ? * FRI")
    @Transactional
    public void processWeeklySettlement(){
        List<Earning> pendingEarnings = earningRepository.findAllByIsSettledFalse();

        if (pendingEarnings.isEmpty()) return;

        Map<Long, BigDecimal> totalsByDriver = pendingEarnings.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDriver().getId(), Collectors.reducing(BigDecimal.ZERO, Earning::getNetAmount,BigDecimal::add)));

        LocalDate settlementDate = LocalDate.now();
        totalsByDriver.forEach((driverId, totalSettlementAmount) ->{
            DriverBalance balance = driverBalanceRepository.findByDriverId(driverId)
                    .orElseGet(() -> {
                        // Crea si no existe
                        DriverBalance newBalance = new DriverBalance();
                        newBalance.setDriverId(driverId);
                        return driverBalanceRepository.save(newBalance);
                    });

            balance.setAvailableBalance(balance.getAvailableBalance().add(totalSettlementAmount));
            balance.setLastUpdatedAt(LocalDateTime.now());
            driverBalanceRepository.save(balance);
        });

        pendingEarnings.forEach(earning -> {
            earning.setSettled(true);
            earning.setSettlementDate(LocalDateTime.now());
        });
        earningRepository.saveAll(pendingEarnings);
    }

    @Transactional
    public void confirmPayout(List<Long> driverIds, String batchReference){
        for (Long driverId : driverIds){
            DriverBalance balance = driverBalanceRepository.findByDriverId(driverId).orElse(null);
            if (balance != null){
                BigDecimal paidAmount = balance.getAvailableBalance();

                if (paidAmount.compareTo(BigDecimal.ZERO) > 0){
                    balance.setAvailableBalance(BigDecimal.ZERO);
                    balance.setLastUpdatedAt(LocalDateTime.now());
                    driverBalanceRepository.save(balance);

                    createPayoutRecord(driverId, paidAmount, batchReference);
                }
            }
        }


    }

    private void createPayoutRecord(Long driverId, BigDecimal amount, String reference) {
        // Aquí invocarías un PayoutRepository para guardar la transacción:
        // Payout payout = new Payout(driverId, amount, reference, LocalDateTime.now());
        // payoutRepository.save(payout);
        // Este paso es crucial para que el conductor vea su historial de pagos.
    }
}
