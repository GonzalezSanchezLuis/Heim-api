package com.heim.api.move.infraestructure.repository;

import com.heim.api.move.domain.entity.Move;
import com.heim.api.move.domain.enums.MoveStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoveRepository extends CrudRepository<Move, Long> {
    Optional<Move> findByMoveIdAndDriver_Id(Long moveId, Long driverId);
    List<Move> findByDriverIdAndStatus(Long driverId, MoveStatus status);
    List<Move> findByUser_UserIdAndStatus(Long userId, MoveStatus status);

    Optional<Move> findByUser_UserIdAndOriginAndDestinationAndStatus(Long userId, String origin, String destination, MoveStatus status);
}

