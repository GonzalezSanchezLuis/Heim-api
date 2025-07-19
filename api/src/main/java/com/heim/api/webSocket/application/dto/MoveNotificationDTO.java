package com.heim.api.webSocket.application.dto;

import com.heim.api.move.application.dto.MoveDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveNotificationDTO {
    private MoveDTO move;
}
