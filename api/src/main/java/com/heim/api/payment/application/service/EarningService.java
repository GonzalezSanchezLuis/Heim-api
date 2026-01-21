package com.heim.api.payment.application.service;

import com.heim.api.move.domain.entity.Move;
import com.heim.api.payment.application.dto.EarningsDTO;
import com.heim.api.payment.domain.DriverBalance;
import com.heim.api.payment.domain.Earning;
import com.heim.api.payment.infraestructure.repository.DriverBalanceRepository;
import com.heim.api.payment.infraestructure.repository.EarningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;


@Service
public class EarningService {
    private static final Logger logger = LoggerFactory.getLogger(EarningService.class);
    private static final int CUTOFF_HOUR = 16;
    private final EarningRepository earningRepository;
    private final DriverBalanceRepository driverBalanceRepository;
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.20");


    @Autowired
    EarningService(EarningRepository earningRepository,
                   DriverBalanceRepository driverBalanceRepository
                   ){
        this.earningRepository = earningRepository;
        this.driverBalanceRepository = driverBalanceRepository;
    }

    public EarningsDTO getDriverEarnings(Long driverId){

        BigDecimal pendingBalance = earningRepository.sumPendingBalanceByDriverId(driverId);

        DriverBalance driverBalance = driverBalanceRepository.findByDriverId(driverId)
                .orElseGet(() -> {
                    DriverBalance newBalance = new DriverBalance();
                    newBalance.setDriverId(driverId);
                    return driverBalanceRepository.save(newBalance);
                });

        BigDecimal availableBalance = driverBalance.getAvailableBalance();
        LocalDate nextPaymentDate = calculateNextPaymentDate();

        LocalDateTime lastSettlementDate = earningRepository.findLastSettlementDateByDriverId(driverId);
        LocalDate lastPaymentDate = lastSettlementDate != null ? lastSettlementDate.toLocalDate() : null;


        EarningsDTO earningsDTO = new EarningsDTO(
                driverId,
                availableBalance,
                pendingBalance,
                lastPaymentDate,
                nextPaymentDate
        );

        logger.info("DATOS DE LA BILLETERA: Disponible={}, Pendiente={}, Próx. Pago={}",
                availableBalance, pendingBalance, nextPaymentDate);

        return  earningsDTO;
    }

    public Earning createPendingEarning(Move move){
        BigDecimal driverShare = BigDecimal.ONE.subtract(COMMISSION_RATE);
        BigDecimal netAmount = move.getPrice().multiply(driverShare);

        Earning earning = new Earning();
        earning.setMove(move);
        earning.setDriver(move.getDriver());
        earning.setNetAmount(netAmount);
        earning.setSettled(false);
        earning.setCreationDate(LocalDateTime.now());

        return earningRepository.save(earning);

    }

    private LocalDate calculateNextPaymentDate(){
        LocalDateTime now = LocalDateTime.now();
        LocalDate nextFriday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).toLocalDate();

        if (now.getDayOfWeek() == DayOfWeek.FRIDAY && now.getHour() >= CUTOFF_HOUR){
            nextFriday = nextFriday.plusWeeks(1);
        }else  if (now.getDayOfWeek().getValue() > DayOfWeek.FRIDAY.getValue()){
            nextFriday = now.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).toLocalDate();
        }
        return  nextFriday;
    }
}
