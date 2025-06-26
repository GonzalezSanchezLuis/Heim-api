package com.heim.api.move.application.service;

import com.heim.api.drivers.application.dto.DriverLocation;
import com.heim.api.drivers.application.dto.TimeAndDistanceDestinationResponse;
import com.heim.api.drivers.application.dto.TimeAndDistanceOriginResponse;
import com.heim.api.drivers.application.service.DistanceCalculatorService;
import com.heim.api.drivers.domain.entity.Driver;
import com.heim.api.drivers.infraestructure.repository.DriverRepository;
import com.heim.api.fcm.domain.entity.FcmToken;
import com.heim.api.hazelcast.application.dto.GeoLocation;
import com.heim.api.hazelcast.service.HazelcastGeoService;
import com.heim.api.move.application.dto.*;
import com.heim.api.move.application.mapper.MoveMapper;
import com.heim.api.notification.application.service.NotificationService;
import com.heim.api.move.domain.entity.Move;
import com.heim.api.move.domain.enums.MoveStatus;
import com.heim.api.move.infraestructure.repository.MoveRepository;
import com.heim.api.users.domain.entity.User;
import com.heim.api.users.infraestructure.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Slf4j
@Service
public class MoveService {
    private static final Logger logger = LoggerFactory.getLogger(MoveService.class);
    private final MoveRepository moveRepository;
    private final NotificationService notificationService;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final HazelcastGeoService hazelcastGeoService;
    private final MoveCacheService tripCacheService;
    private final DistanceCalculatorService distanceCalculatorService;
    private final MoveMapper moveMapper;


    @Autowired
    public MoveService(MoveRepository moveRepository,
                       NotificationService notificationService,
                       DriverRepository driverRepository,
                       UserRepository userRepository,
                       HazelcastGeoService hazelcastGeoService,
                       MoveCacheService tripCacheService,
                       DistanceCalculatorService distanceCalculatorService,
                       MoveMapper moveMapper
                       ) {

        this.moveRepository = moveRepository;
        this.notificationService = notificationService;
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.hazelcastGeoService = hazelcastGeoService;
        this.tripCacheService = tripCacheService;
        this.distanceCalculatorService = distanceCalculatorService;
        this.moveMapper = moveMapper;

    }

    private static final int MAX_NOTIFICATION_ATTEMPTS = 3;

    private static final String[] NOTIFICATION_MESSAGES = {
            "¡Nuevo viaje disponible cerca de ti!",
            "¡No pierdas la oportunidad, viaje disponible cerca!",
            "Último aviso: viaje disponible para aceptar.",
    };


    public MoveConfirmationResponse confirmMove(MoveRequest moveRequest){
        try {
            double latitude = moveRequest.getOriginLat();
            double longitude = moveRequest.getOriginLng();

            List<Long> nearbyDrivers = Optional.ofNullable(
                    hazelcastGeoService.findNearbyDriversDynamically(latitude, longitude)
            ).orElse(Collections.emptyList());


            if (!nearbyDrivers.isEmpty()){
               // tripCacheService.storeTrip(tripRequest.getUserId(), tripRequest);
                Optional<Move> existingMove = moveRepository.findByUser_UserIdAndOriginAndDestinationAndStatus(
                        moveRequest.getUserId(),
                        moveRequest.getOrigin(),
                        moveRequest.getDestination(),
                        MoveStatus.REQUESTED
                );
                Move move;
                if (existingMove.isPresent()) {
                    move = existingMove.get();
                    log.info("Ya existe un viaje similar para este usuario.");
                } else {
                    move = createMove(moveRequest);
                }

                notifyDriversLimited(move, nearbyDrivers);
                   // notificationService.notify(FcmToken.OwnerType.DRIVER, driverId, "Nuevo viaje disponible", "¡Tienes un nuevo viaje disponible cerca!");

                return new MoveConfirmationResponse("Enviando solicitud a conductores cercanos...");
            }else {
                return new MoveConfirmationResponse("No hay conductores disponibles cerca por ahora.");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Move createMove(MoveRequest moveRequest) {
        User user = userRepository.findById(moveRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + moveRequest.getUserId()));

        Optional<Move> existingMove = moveRepository.findByUser_UserIdAndOriginAndDestinationAndStatus(
                user.getUserId(),
                moveRequest.getOrigin(),
                moveRequest.getDestination(),
                MoveStatus.REQUESTED
        );

        if (existingMove.isPresent()) {
            log.info("Ya existe un viaje similar para este usuario.");
            return existingMove.get(); // Devuelve el viaje existente en lugar de crear uno nuevo
        }

        String rawPrice = String.valueOf(moveRequest.getPrice());  // Ejemplo: "5.843,09 COP"
        String cleanedPrice = rawPrice.replace(".", "").replace(",", ".").replace(" COP", "");
        BigDecimal price = new BigDecimal(cleanedPrice);

        Move move = new Move();
        move.setUser(user);
        move.setOrigin(moveRequest.getOrigin());
        move.setDestination(moveRequest.getDestination());
        move.setOriginLat(moveRequest.getOriginLat());
        move.setOriginLng(moveRequest.getOriginLng());
        move.setDestinationLat(moveRequest.getDestinationLat());
        move.setDestinationLng(moveRequest.getDestinationLng());
        move.setTypeOfMove(moveRequest.getTypeOfMove());
        move.setPrice(price);
        move.setPaymentMethod(moveRequest.getPaymentMethod());
        move.setRequestTime(LocalDateTime.now());
        move.setStatus(MoveStatus.REQUESTED);

        move = moveRepository.save(move);

        // Notificar al conductor que hay un nuevo viaje disponible
       // notifyDriversAboutNewTrip(trip);

        return move;
    }


    @Transactional
    public MoveDTO assignDriverToMove(AcceptMoveRequest acceptMoveRequest) {
        Optional<Move> moveOptional = moveRepository.findById(acceptMoveRequest.getMoveId());

        if (moveOptional.isPresent()) {
            Move move = moveOptional.get();

            Driver driver = driverRepository.findById(acceptMoveRequest.getDriverId())
                    .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

            move.setDriver(driver);
            move.setStatus(MoveStatus.ASSIGNED);
            move.setStartTime(LocalDateTime.now());

          Move savedMove =   moveRepository.save(move);
            logger.info("Viaje actualizado exitosamente: {}", savedMove);

            // Notificar al usuario que el viaje ha sido aceptado por un conductor
            try {
                sendNotificationToUser(move);
                logger.info("Notificando al usuario");
            }catch (Exception e){
                logger.error("Error al enviar notificación", e);

            }
            return moveMapper.toDTO(savedMove);
        }

        return null;
    }

    @Transactional
    public void markDriverArrived(MovingStatusesDTO movingStatusesDTO) {
        Move move = moveRepository.findById(movingStatusesDTO.getMoveId())
                .orElseThrow(() -> new EntityNotFoundException("Mudanza no encontrada"));

        move.setStatus(MoveStatus.DRIVER_ARRIVED);

       // notificationService.notifyUser(move.getUser(), "🚗 Tu conductor ha llegado al punto de recogida.");
        moveRepository.save(move);
    }

    @Transactional
    public void startMove(MovingStatusesDTO movingStatusesDTO){
        Move move = moveRepository.findById(movingStatusesDTO.getMoveId()).orElseThrow(()-> new EntityNotFoundException("Mudanza no encontrada"));
        move.setStatus(MoveStatus.MOVING_STARTED);

        move.setStartTime(movingStatusesDTO.getTimestamp() != null ? movingStatusesDTO.getTimestamp() : LocalDateTime.now());

       // notificationService.notifyUser(move.getUser(), "📍 Tu mudanza ha comenzado.");
        moveRepository.save(move);

    }



    @Transactional
    public Move completeMove(MovingStatusesDTO movingStatusesDTO) {
        Optional<Move> moveOptional = moveRepository.findById(movingStatusesDTO.getMoveId());

        if (moveOptional.isPresent()) {
            Move move = moveOptional.get();
            move.setStatus(MoveStatus.MOVE_COMPLETE);
            move.setEndTime(LocalDateTime.now());

            moveRepository.save(move);

            // Notificar al usuario que el viaje ha sido completado
            sendNotificationToUser(move);

            return move;
        }

        return null;
    }



    private void sendNotificationToUser(Move move) {
        // Notificar al usuario cuando el viaje cambia de estado
        String message = "";
        GeoLocation driverLocation = hazelcastGeoService.getDriverLocation(move.getDriver().getId());
        Map<String, String> data = buildMoveDataForUser(move, driverLocation);

        switch (move.getStatus()) {
            case ASSIGNED:
                message = "¡Tu viaje ha sido aceptado por un conductor!";
                break;
            case MOVE_COMPLETE:
                message = "¡Tu viaje ha sido completado!";
                break;
            default:
                message = "Actualización de tu viaje";
        }


       notificationService.notify(
               FcmToken.OwnerType.USER,
               move.getUser().getUserId(),
               "Actualización de tu viaje", "Tu viaje ha sido aceptado.",
               data,
               message
               );

    }

    private void notifyDriversLimited(Move move, List<Long> nearbyDrivers){
        log.info("Notificando a conductores, total conductores: {}", nearbyDrivers.size());

        List<DriverLocation> driverLocations = new ArrayList<>();
        for (Long driverId: nearbyDrivers){
            GeoLocation geoLocation =  hazelcastGeoService.getDriverLocation(driverId);
            driverLocations.add( new DriverLocation(driverId, geoLocation.getLatitude(), geoLocation.getLongitude()));
        }

        Map<Long, DriverLocation> driverLocationMap = driverLocations.stream().collect(Collectors.toMap(DriverLocation::getDriverId, dl -> dl));

        // Obtener ubicaciones de conductores
        /*List<DriverLocation> driverLocations = nearbyDrivers.stream()
                .map(driverId -> {
                    GeoLocation location = hazelcastGeoService.getDriverLocation(driverId);
                    return new DriverLocation(driverId, location.getLatitude(), location.getLongitude());
                })
                .toList();*/

        // Calcular distancias y tiempos de origen
        Map<Long, TimeAndDistanceOriginResponse> driverDistances = distanceCalculatorService
                .calculateDistancesToUserForMultipleDrivers(driverLocations, move.getOriginLat(), move.getOriginLng());


        // Calcular distancias y tiempos de destino
        Map<Long, TimeAndDistanceDestinationResponse> distancesToDestination =
                distanceCalculatorService.calculateDistancesToDestinationForMultipleDrivers(driverLocations,move.getDestinationLat(), move.getDestinationLng());

        for (Map.Entry<Long, TimeAndDistanceOriginResponse> entry : driverDistances.entrySet()) {
            Long driverId = entry.getKey();
            TimeAndDistanceOriginResponse response = entry.getValue();

            if (response != null) {
                log.info("Conductor ID {}: distancia = {}, tiempo = {}",
                        driverId, response.getDistance(), response.getEstimatedTimeOfArrival());
            } else {
                log.warn("No se obtuvo distancia/tiempo para el conductor ID {}", driverId);
            }
        }

        for (Long driverId : nearbyDrivers){
            int attempts = tripCacheService.getNotificationCount(move.getMoveId(), driverId);
            if (attempts < MAX_NOTIFICATION_ATTEMPTS){
                String message = NOTIFICATION_MESSAGES[attempts];
                log.info("Notificando conductor con ID: {}", driverId);

                Map<String, String> moveData = buildMoveData(
                        move, driverDistances.get(driverId),
                        distancesToDestination.get(driverId),
                        driverLocationMap.get(driverId));


                notificationService.notify(
                        FcmToken.OwnerType.DRIVER,
                        driverId,
                        "Nuevo viaje disponible",         // Título
                        "Tienes un nuevo viaje",          // Cuerpo (podrías personalizarlo más)
                        moveData,                         // Datos
                        message                           // Mensaje adicional
                );
                tripCacheService.incrementNotificationCount(move.getMoveId(), driverId);
            }else {
                log.info("No se notificará más al conductor {} para el viaje {} para evitar spam.", driverId, move.getMoveId());
            }
        }
    }

    private Map<String, String> buildMoveData(
            Move move,
            TimeAndDistanceOriginResponse distanceResponse,
            TimeAndDistanceDestinationResponse destinationResponse,
            DriverLocation driverLocation) {
        Map<String, String> moveData = new HashMap<>();
        moveData.put("moveId", String.valueOf(move.getMoveId()));
        moveData.put("origin", move.getOrigin());
        moveData.put("destination", move.getDestination());
        moveData.put("originLat", String.valueOf(move.getOriginLat()));
        moveData.put("originLng", String.valueOf(move.getOriginLng()));
        moveData.put("destinationLat", String.valueOf(move.getDestinationLat()));
        moveData.put("destinationLng", String.valueOf(move.getDestinationLng()));
        moveData.put("typeOfMove", String.valueOf(move.getTypeOfMove()));
        moveData.put("price", move.getPrice().toPlainString());
        moveData.put("paymentMethod", String.valueOf(move.getPaymentMethod()));

        log.info("DATOS DE LA DISTANCIA {}" , distanceResponse);

        moveData.put("distance", distanceResponse.getDistance());
        moveData.put("estimatedTimeOfArrival", distanceResponse.getEstimatedTimeOfArrival());

        moveData.put("distanceToDestination", destinationResponse.getDistanceToDestination());
        moveData.put("timeToDestination", destinationResponse.getTimeToDestination());

        moveData.put("driverLat", String.valueOf(driverLocation.getLatitude()));
        moveData.put("driverLng", String.valueOf(driverLocation.getLongitude()));
        moveData.put("originLat", String.valueOf(move.getOriginLat()));
        moveData.put("originLng", String.valueOf(move.getOriginLng()));
        moveData.put("destinationLat", String.valueOf(move.getDestinationLat()));
        moveData.put("destinationLng", String.valueOf(move.getDestinationLng()));
        moveData.put("role", "DRIVER");


        return moveData;
    }

    private Map<String, String> buildMoveDataForUser(
            Move move,
            GeoLocation driverLocation
            ) {
        Map<String, String> data = new HashMap<>();
        logger.info("COORDENADAS DEL CONDUCTOR ENVIADAS AL CLIENTE {}", data);

        data.put("moveId", move.getMoveId().toString());
        data.put("status", move.getStatus().toString());

        if (move.getDriver() != null) {
            logger.info("📍 LOCATION: {}, {}", driverLocation.getLatitude(), driverLocation.getLongitude());
            data.put("driverLat", String.valueOf(driverLocation.getLatitude()));
            data.put("driverLng", String.valueOf(driverLocation.getLongitude()));
            data.put("role", "USER");
        }


        return data;
    }


    public List<Move> getTripsByUser(Long userId) {
        return moveRepository.findByUser_UserIdAndStatus(userId, MoveStatus.REQUESTED);
    }

    public List<Move> getTripsByDriver(Long driverId, MoveStatus status) {
        return moveRepository.findByUser_UserIdAndStatus(driverId, status);
    }

    public Optional<Move> getTripForDriver(Long moveId, Long driverId) {
        return moveRepository.findByMoveIdAndDriver_Id(moveId, driverId);
    }

    private void notifyDriversAboutNewTrip(Move move) {
        // notificationService.notify(FcmToken.OwnerType.DRIVER, trip.getDriver().getId(), "Nuevo viaje disponible", "¡Tienes un nuevo viaje disponible cerca!");
    }
 

   /* private boolean isDriverAvailable(Long driverId){
        List<Trip> activeTrips = tripRepository.findByUser_UserIdAndStatus(driverId, TripStatus.IN_PROGRESS);
        return activeTrips.isEmpty();
    }

    public List<Long> findNearbyDrivers(double latitude, double longitude) {
        return hazelcastGeoService.findNearbyDriversDynamically(latitude, longitude);
    }*/
    }
