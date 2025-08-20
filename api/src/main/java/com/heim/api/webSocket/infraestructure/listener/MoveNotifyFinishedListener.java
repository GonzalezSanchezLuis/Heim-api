package com.heim.api.webSocket.infraestructure.listener;


import com.heim.api.webSocket.domain.entity.event.MoveAssignedEvent;
import com.heim.api.webSocket.domain.entity.event.MoveFinishedEvent;
import com.heim.api.webSocket.service.MoveFinishedSocketNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MoveNotifyFinishedListener {
    private final MoveFinishedSocketNotifyService moveFinishedSocketNotifyService;

    public   MoveNotifyFinishedListener(MoveFinishedSocketNotifyService moveFinishedSocketNotifyService){
        this.moveFinishedSocketNotifyService = moveFinishedSocketNotifyService;

   }

    @EventListener
    public void handleMoveAssignedEventFinished(MoveFinishedEvent event) {
        moveFinishedSocketNotifyService.notifyMoveFinished(event.getMoveId(), event.getPaymentInfo());
    }
}
