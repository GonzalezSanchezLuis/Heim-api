package com.heim.api.payment.application.service;


import com.heim.api.move.application.service.MoveService;
import com.heim.api.payment.application.dto.EarningsDTO;
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

    @Autowired
    EarningService(EarningRepository earningRepository){
        this.earningRepository = earningRepository;
    }

    public EarningsDTO getDriverEarnings(Long driverId){
        BigDecimal  earnedBalance = earningRepository.sumUnpaidBalanceByDriverId(driverId);
        LocalDate nextPaymentDate = calculateNextPaymentDate();
        LocalDate lastPaymentDate = earningRepository.findLastPaymentDateByDriverId(driverId);

        EarningsDTO   earningsDTO =  new EarningsDTO(
                driverId,
                earnedBalance,
                lastPaymentDate,
                nextPaymentDate
        );

        logger.info("DATOS DE LA BILLETERA {},", earningsDTO);

        return  earningsDTO;
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
