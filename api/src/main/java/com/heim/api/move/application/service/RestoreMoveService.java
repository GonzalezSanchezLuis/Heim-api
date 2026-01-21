package com.heim.api.move.application.service;

import com.heim.api.move.application.dto.RestoreMoveResponseDTO;
import com.heim.api.move.application.mapper.ActiveMoveMapper;
import com.heim.api.move.domain.enums.MoveStatus;
import com.heim.api.move.infraestructure.repository.MoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RestoreMoveService {

    private final MoveRepository moveRepository;
    private final ActiveMoveMapper activeMoveMapper;

    @Autowired
    public RestoreMoveService(MoveRepository moveRepository, ActiveMoveMapper activeMoveMapper) {
        this.moveRepository = moveRepository;
        this.activeMoveMapper = activeMoveMapper;
    }

    public Optional<RestoreMoveResponseDTO> getActiveMoveForDriver(Long driverId, Long moveId) {
        try {
            List<MoveStatus> activeStatuses = List.of(
                    MoveStatus.ASSIGNED,
                    MoveStatus.MOVING_STARTED
            );

            return moveRepository
                    .findActiveMoveByDriverIdAndMoveId(moveId, driverId, activeStatuses)
                    .map(activeMoveMapper::toDTO);
        } catch (Exception e) {
            System.err.println("❌ Error al obtener la mudanza activa para driverId " + driverId + " y moveId " + moveId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}


