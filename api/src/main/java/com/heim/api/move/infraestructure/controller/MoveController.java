package com.heim.api.move.infraestructure.controller;

import com.heim.api.move.application.dto.*;
import com.heim.api.move.application.service.MoveService;
import com.heim.api.move.domain.entity.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/move/")
@CrossOrigin("*")
public class MoveController {

    private final MoveService moveService;

    @Autowired
    public MoveController(MoveService moveService) {
        this.moveService = moveService;
    }

    @PostMapping("confirm")
    public ResponseEntity<MoveConfirmationResponse> findNearbyDrivers(@RequestBody MoveRequest moveRequest) {
        MoveConfirmationResponse response = moveService.confirmMove(moveRequest);
        return ResponseEntity.ok(response);
    }

    // Endpoint para crear un viaje
    @PostMapping("create")
    public ResponseEntity<Move> createMove(@RequestBody MoveRequest moveRequest) {
        // Se pasa la información del usuario y el viaje
        Move move = moveService.createMove(moveRequest);
        return ResponseEntity.ok(move);
    }


    @PutMapping("accept/{moveId}")
    public ResponseEntity<MoveDTO> assignDriver(@PathVariable Long moveId, @RequestBody AcceptMoveRequest acceptMoveRequest) {
        acceptMoveRequest.setMoveId(moveId);
        MoveDTO moveDTO = moveService.assignDriverToMove(acceptMoveRequest);
        return moveDTO != null ? ResponseEntity.ok(moveDTO) : ResponseEntity.notFound().build();
    }

    @PatchMapping("driver-arrived")
    public ResponseEntity<String> updateMoveStatus(@RequestBody MovingStatusesDTO movingStatusesDTO){
        if (movingStatusesDTO.getMoveId() == null || movingStatusesDTO.getDriverId() == null){
            return ResponseEntity.badRequest().body("Faltan datos obligatorios");
        }


        System.out.println("📥 Datos recibidos: " + movingStatusesDTO);
        Long moveId = movingStatusesDTO.getMoveId();  // <-- AQUÍ llega null
        Long driverId = movingStatusesDTO.getDriverId();

        // Verificación manual
        if (moveId == null || driverId == null) {
            return ResponseEntity.badRequest().body("❌ moveId o driverId es null");
        }
        moveService.markDriverArrived(movingStatusesDTO);
        return ResponseEntity.ok("Estado actualizado a DRIVER_ARRIVED");
    }

    @PatchMapping("start")
    public ResponseEntity<String> starMove(@RequestBody MovingStatusesDTO movingStatusesDTO){
        moveService.startMove(movingStatusesDTO);
        return  ResponseEntity.ok("Mudanza iniciada");
    }

    
    @PatchMapping("complete")
    public ResponseEntity<Move> completeTrip(@RequestBody MovingStatusesDTO movingStatusesDTO) {
        Move move = moveService.completeMove(movingStatusesDTO);
        return move != null ? ResponseEntity.ok(move) : ResponseEntity.notFound().build();
    }


}

