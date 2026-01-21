package com.heim.api.move.infraestructure.repository;

import com.heim.api.move.domain.entity.Move;
import com.heim.api.move.domain.enums.MoveStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoveRepository extends CrudRepository<Move, Long> {
    Optional<Move> findByMoveIdAndDriver_Id(@Param("moveId") Long moveId, @Param("driverId") Long driverId);
    List<Move> findByDriverIdAndStatus(Long driverId, MoveStatus status);
    List<Move> findByUser_UserIdAndStatus(Long userId, MoveStatus status);
    Optional<Move> findByUser_UserIdAndOriginAndDestinationAndStatus(Long userId, String origin, String destination, MoveStatus status);
    Optional<Move> findById(Long moveId);

    @Query("""
    SELECT m FROM Move m
    WHERE m.id = :moveId
      AND m.driver.id = :driverId
      AND m.status IN (:statuses)
""")
    Optional<Move> findActiveMoveByDriverIdAndMoveId(
            @Param("moveId") Long moveId,
            @Param("driverId") Long driverId,
            @Param("statuses") List<MoveStatus> statuses
    );


}

