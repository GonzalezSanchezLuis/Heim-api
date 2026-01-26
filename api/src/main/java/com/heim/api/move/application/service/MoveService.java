package com.heim.api.move.application.service;

import com.heim.api.drivers.application.dto.DriverLocation;
import com.heim.api.drivers.application.dto.TimeAndDistanceDestinationResponse;
import com.heim.api.drivers.application.dto.TimeAndDistanceOriginResponse;
import com.heim.api.drivers.application.service.DistanceCalculatorService;
import com.heim.api.drivers.domain.entity.Driver;
import com.heim.api.drivers.infraestructure.repository.DriverRepository;
import com.heim.api.exceptions.NotFoundException;
import com.heim.api.fcm.domain.entity.FcmToken;
import com.heim.api.hazelcast.application.dto.GeoLocation;
import com.heim.api.hazelcast.service.HazelcastGeoService;
import com.heim.api.move.application.dto.*;
import com.heim.api.move.application.mapper.MoveMapper;
import com.heim.api.move.application.mapper.MoveSummaryMapper;
import com.heim.api.move.application.mapper.MovingHistoryMapper;
import com.heim.api.payment.application.dto.PaymentRequest;
import com.heim.api.payment.application.dto.PaymentResponse;
import com.heim.api.payment.application.service.EarningService;
import com.heim.api.payment.application.service.PaymentService;
import com.heim.api.payment.domain.Earning;
import com.heim.api.payment.infraestructure.repository.EarningRepository;
import com.heim.api.users.application.dto.UserPaymentRequest;
import com.heim.api.users.domain.entity.User;
import com.heim.api.webSocket.domain.entity.event.MoveAssignedEvent;
import com.heim.api.notification.application.service.NotificationService;
import com.heim.api.move.domain.entity.Move;
import com.heim.api.move.domain.enums.MoveStatus;
import com.heim.api.move.infraestructure.repository.MoveRepository;
import com.heim.api.users.infraestructure.repository.UserRepository;
import com.heim.api.webSocket.application.dto.MoveNotificationDTO;
import com.heim.api.webSocket.domain.entity.event.MoveAssignedUserEvent;
import com.heim.api.webSocket.domain.entity.event.MoveFinishedEvent;
import com.heim.api.webSocket.infraestructure.listener.MoveNotificationUserFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MoveNotificationUserFactory moveNotificationUserFactory;
    private final PaymentService paymentService;
    private final MovingHistoryMapper movingHistoryMapper;
    private final EarningRepository earningRepository;
    private final EarningService earningService;




    @Autowired
    public MoveService(MoveRepository moveRepository,
                       NotificationService notificationService,
                       DriverRepository driverRepository,
                       UserRepository userRepository,
                       HazelcastGeoService hazelcastGeoService,
                       MoveCacheService tripCacheService,
                       DistanceCalculatorService distanceCalculatorService,
                       MoveMapper moveMapper,
                        ApplicationEventPublisher applicationEventPublisher,
                       MoveNotificationUserFactory moveNotificationUserFactory,
                       PaymentService paymentService,
                       MovingHistoryMapper movingHistoryMapper,
                       EarningRepository earningRepository,
                       EarningService earningService
                       ) {

        this.moveRepository = moveRepository;
        this.notificationService = notificationService;
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.hazelcastGeoService = hazelcastGeoService;
        this.tripCacheService = tripCacheService;
        this.distanceCalculatorService = distanceCalculatorService;
        this.moveMapper = moveMapper;
        this.applicationEventPublisher = applicationEventPublisher;
        this.moveNotificationUserFactory = moveNotificationUserFactory;
        this.paymentService = paymentService;
        this.movingHistoryMapper = movingHistoryMapper;
        this.earningRepository =  earningRepository;
        this.earningService = earningService;

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
        com.heim.api.users.domain.entity.User user = userRepository.findById(moveRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + moveRequest.getUserId()));

        Optional<Move> existingMove = moveRepository.findByUser_UserIdAndOriginAndDestinationAndStatus(
                user.getUserId(),
                moveRequest.getOrigin(),
                moveRequest.getDestination(),
                MoveStatus.REQUESTED
        );

        if (existingMove.isPresent()) {
            log.info("Ya existe un viaje similar para este usuario.");
            return existingMove.get();
        }

      //  String rawPrice = String.valueOf(moveRequest.getPrice());
      //  String cleanedPrice = rawPrice.replace(".", "").replace(",", ".").replace(" COP", "");
       // BigDecimal price = new BigDecimal(cleanedPrice);
        BigDecimal price = moveRequest.getPrice();
        price = price.setScale(2, RoundingMode.HALF_UP);

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
        move.setDistanceKm(moveRequest.getDistanceKm());
        move.setDurationMin(moveRequest.getEstimatedTime());
        move.setRequestTime(LocalDateTime.now());
        move.setStatus(MoveStatus.REQUESTED);

        move = moveRepository.save(move);
        return move;
    }


    @Transactional
    public MoveDTO assignDriverToMove(AcceptMoveRequest acceptMoveRequest) {
        Optional<Move> moveOptional = moveRepository.findById(acceptMoveRequest.getMoveId());
        System.out.println("DATO QUE VIENE DESDE EL CLIENTE " +acceptMoveRequest);

        if (moveOptional.isPresent()) {
            Move move = moveOptional.get();

            Driver driver = driverRepository.findByUserId(acceptMoveRequest.getDriverId()).orElseThrow(() -> new IllegalArgumentException("Driver not found"));

            move.setDriver(driver);
            move.setStatus(MoveStatus.ASSIGNED);
            move.setStartTime(LocalDateTime.now());

          Move savedMove =   moveRepository.save(move);
            logger.info("Viaje actualizado exitosamente: {}", savedMove);

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
        notificationService.notifyUser(FcmToken.OwnerType.USER, move.getUser().getUserId(), "\uD83D\uDE9B ¡Tu conductor ha llegado!",
                "Ya estamos aquí, justo a tiempo para ayudarte a dar este gran paso.");
        moveRepository.save(move);
    }


    @Transactional
    public void startMove(MovingStatusesDTO movingStatusesDTO){
        Move move = moveRepository.findById(movingStatusesDTO.getMoveId()).orElseThrow(()-> new EntityNotFoundException("Mudanza no encontrada"));
        move.setStatus(MoveStatus.MOVING_STARTED);

        move.setStartTime(movingStatusesDTO.getTimestamp() != null ? movingStatusesDTO.getTimestamp() : LocalDateTime.now());

        notificationService.notifyUser(FcmToken.OwnerType.USER, move.getUser().getUserId(), "\uD83D\uDE9A La mudanza está en marcha",
                "Todo va según lo planeado. Tu nueva etapa está cada vez más cerca.");
        moveRepository.save(move);

    }


    @Transactional
    public Move completeMove(MovingStatusesDTO movingStatusesDTO) {
        Optional<Move> moveOptional = moveRepository.findById(movingStatusesDTO.getMoveId());

        if (moveOptional.isPresent()) {
            Move move = moveOptional.get();
            com.heim.api.users.domain.entity.User user = move.getUser();
            move.setStatus(MoveStatus.MOVE_COMPLETE);
            move.setEndTime(LocalDateTime.now());

            earningService.createPendingEarning(move);

            PaymentRequest paymentRequest = getPaymentRequest(user, move);

            String wavaPaymentUrl;
            try {
                wavaPaymentUrl = paymentService.createPaymentLink(paymentRequest);
            } catch (Exception e) {
                logger.error("Error al generar el link de pago de Wava", e);
                // Aquí podrías manejar el error, por ejemplo, enviando una notificación
                wavaPaymentUrl = "heim://pay";
            }



            PaymentResponse paymentResponse = getPaymentResponse(move, wavaPaymentUrl);
            moveRepository.save(move);

            notificationService.notifyUser(FcmToken.OwnerType.USER, move.getUser().getUserId(), "\uD83C\uDFE1 ¡Tu mudanza fue completada con éxito!",
                    "¡Gracias por confiar en nosotros para este gran paso! Te deseamos lo mejor en tu nuevo hogar. \uD83E\uDDE1");

            MoveFinishedEvent event = new MoveFinishedEvent(move.getMoveId(), paymentResponse);
            applicationEventPublisher.publishEvent(event);
            logger.info("ENVIANDO DATOS DE PAGO MEDIANTE WEBSOCKET {}", event);

            return move;
        }

        return null;
    }

    @NotNull
    private static PaymentRequest getPaymentRequest(com.heim.api.users.domain.entity.User user, Move move) {
        PaymentRequest paymentRequest = new PaymentRequest();
        BigDecimal priceFromDb = move.getPrice();
        BigDecimal finalPrice = priceFromDb.setScale(0, RoundingMode.HALF_UP);


        logger.info("PRECIO ORIGINAL EN DB: {}", priceFromDb);
        logger.info("PRECIO FORMATEADO ENVIADO A WAVA: {}", finalPrice);

        paymentRequest.setDescription("Servicio de mudanza");
        paymentRequest.setAmount(finalPrice);
        UserPaymentRequest userRequestPayment = new UserPaymentRequest();
        userRequestPayment.setFirstName(user.getFullName());
        userRequestPayment.setEmail(user.getEmail());
        userRequestPayment.setPhoneNumber(user.getPhone());
        userRequestPayment.setCountry("CO");
        userRequestPayment.setIdNumber("CC");

        paymentRequest.setRedirectLink("heim://pay");
        paymentRequest.setUserPaymentRequest(userRequestPayment);
        paymentRequest.setOrderKey(String.valueOf(move.getMoveId()));
        return paymentRequest;
    }

    private PaymentResponse getPaymentResponse(Move move, String wavaPaymentUrl) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentURL(wavaPaymentUrl); // ✅ Usar el URL dinámico
        paymentResponse.setPaymentMethod(move.getPaymentMethod());
        paymentResponse.setAmount(move.getPrice());
        paymentResponse.setCurrency("COP");
        paymentResponse.setMoveId(move.getMoveId());
        paymentResponse.setOrigin(move.getOrigin());
        paymentResponse.setDestination(move.getDestination());
        paymentResponse.setDurationMin(move.getDurationMin());
        paymentResponse.setDistanceKm(move.getDistanceKm());
        return paymentResponse;
    }


    public MoveStatus getMoveStatus(Long moveId) {
        Move move = moveRepository.findById(moveId)
                .orElseThrow(() -> new EntityNotFoundException("Mudanza no encontrada"));
        return move.getStatus();
    }


    private void sendNotificationToUser(Move move) {
        String message = "";
        GeoLocation driverLocation = hazelcastGeoService.getDriverLocation(move.getDriver().getId());
        Map<String, String> data = buildMoveDataForUser(move, driverLocation);
        logger.info("DATOS DE LA MUDANZA PARA EL USUARIO {}",data);

        switch (move.getStatus()) {
            case ASSIGNED:
                message = "\uD83D\uDE9B ¡Ya vamos por ti!";
                break;
            case MOVE_COMPLETE:
                message = "¡Tu viaje ha sido completado!";
                break;
            default:
                message = "Actualización de tu viaje";
        }

        com.heim.api.users.domain.entity.User driverUser = move.getDriver().getUser();

       notificationService.notify(
               FcmToken.OwnerType.USER,
               move.getUser().getUserId(),
               "\uD83D\uDE9B ¡Tu mudanza ya tiene conductor!", "Tranquilo, ya estamos en camino para ayudarte a empezar esta nueva etapa.",
               data,
               message
               );


        MoveNotificationUserResponse moveNotification = moveNotificationUserFactory.build(move);
        if (driverLocation != null){
            moveNotification.setDriverLat(driverLocation.getLatitude());
            moveNotification.setDriverLng(driverLocation.getLongitude());
        }
        applicationEventPublisher.publishEvent(new MoveAssignedUserEvent(moveNotification, move.getUser().getUserId()));
        logger.info("Enviando datos al usuario por WEBSOCKET: {}",moveNotification);
    }

    private void notifyDriversLimited(Move move, List<Long> nearbyDrivers){
        log.info("Notificando a conductores, total conductores: {}", nearbyDrivers.size());

        List<DriverLocation> driverLocations = new ArrayList<>();
        for (Long driverId: nearbyDrivers){
            GeoLocation geoLocation =  hazelcastGeoService.getDriverLocation(driverId);
            driverLocations.add( new DriverLocation(driverId, geoLocation.getLatitude(), geoLocation.getLongitude()));
        }

        Map<Long, DriverLocation> driverLocationMap = driverLocations.stream().collect(Collectors.toMap(DriverLocation::getDriverId, dl -> dl));

        Map<Long, TimeAndDistanceOriginResponse> driverDistances = distanceCalculatorService
                .calculateDistancesToUserForMultipleDrivers(driverLocations, move.getOriginLat(), move.getOriginLng());


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
            Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new NoSuchElementException("Driver no encontrado"));
            Long userId = driver.getUser().getUserId();

            int attempts = tripCacheService.getNotificationCount(move.getMoveId(), driverId);
            if (attempts < MAX_NOTIFICATION_ATTEMPTS){
                String message = NOTIFICATION_MESSAGES[attempts];
                log.info("Notificando conductor con ID: {}, USERID: {}", driverId, userId);

                Map<String, String> moveData = buildMovingInformationDriver(
                        move, driverDistances.get(driverId),
                        distancesToDestination.get(driverId),
                        driverLocationMap.get(driverId));


                notificationService.notify(
                        FcmToken.OwnerType.DRIVER,
                        userId,
                        "\uD83D\uDEA8 ¡Una nueva mudanza necesita de ti!",
                        "Tu ayuda puede marcar la diferencia para alguien que se muda hoy.",
                        moveData,
                        message);
                DriverLocation driverLocation = driverLocationMap.get(driverId);

                MoveDTO moveDTO =  moveMapper.toDTO(move);
                String avatar = Optional.ofNullable(move.getUser())
                        .map(User::getUrlAvatarProfile)
                        .orElse("");


                TimeAndDistanceDestinationResponse destinationData = distancesToDestination.get(driverId);
                if (destinationData != null) {
                   moveDTO.setDistanceToDestination(destinationData.getTimeToDestination());
                   moveDTO.setTimeToDestination(destinationData.getDistanceToDestination());
                 }
                if(move.getUser() != null){
                    moveDTO.setFullName(move.getUser().getFullName());
                    moveDTO.setUserId(move.getUser().getUserId());
                }

                if (driverLocation != null) {
                    moveDTO.setDriverLat(driverLocation.getLatitude());
                    moveDTO.setDriverLng(driverLocation.getLongitude());
                }

                TimeAndDistanceOriginResponse originData = driverDistances.get(driverId);
                if (originData != null) {
                    moveDTO.setEstimatedTimeOfArrival(originData.getEstimatedTimeOfArrival());
                    moveDTO.setDistance(originData.getDistance());
                }

                MoveNotificationDTO notification = new MoveNotificationDTO(moveDTO);
                applicationEventPublisher.publishEvent(new MoveAssignedEvent(notification,userId));
                tripCacheService.incrementNotificationCount(move.getMoveId(), driverId);
                logger.info("ENVIANDO DATOS DE LA MUDANZA MEDIANTE WEBSOCKET {}", moveDTO);

            }else {
                log.info("No se notificará más al conductor {} para el viaje {} para evitar spam.", driverId, move.getMoveId());
            }
        }
    }

    private Map<String, String> buildMovingInformationDriver(
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
        moveData.put("typeOfMove", String.valueOf(move.getTypeOfMove().name()));
        moveData.put("price", move.getPrice().toPlainString());
        moveData.put("paymentMethod", String.valueOf(move.getPaymentMethod()));

        log.info("DATOS DE LA DISTANCIA {}" , distanceResponse);

        moveData.put("distance", distanceResponse.getDistance());
        moveData.put("estimatedTimeOfArrival", distanceResponse.getEstimatedTimeOfArrival());
        moveData.put("distanceToDestination", destinationResponse.getDistanceToDestination());
        moveData.put("timeToDestination", destinationResponse.getTimeToDestination());

        // Coordenadas del conductor
        log.info("VALIDACIÓN: Driver Lat/Lng recibidos son VÁLIDOS: {} / {}", driverLocation.getLatitude(), driverLocation.getLongitude());

        moveData.put("driverLat", String.valueOf(driverLocation.getLatitude()));
        moveData.put("driverLng", String.valueOf(driverLocation.getLongitude()));
        moveData.put("originLat", String.valueOf(move.getOriginLat()));
        moveData.put("originLng", String.valueOf(move.getOriginLng()));
        moveData.put("destinationLat", String.valueOf(move.getDestinationLat()));
        moveData.put("destinationLng", String.valueOf(move.getDestinationLng()));
        moveData.put("role", "DRIVER");

        //DATOS DEL USUARIO
        if (move.getUser() != null) {
            moveData.put("userName", move.getUser().getFullName());
            moveData.put("avatarProfile", move.getUser().getUrlAvatarProfile() != null ? move.getUser().getUrlAvatarProfile() : "");
          //  moveData.put("userPhone", move.getUser().getPhone());
        }
        return moveData;
    }

    private Map<String, String> buildMoveDataForUser(Move move, GeoLocation driverLocation) {
        Map<String, String> data = new HashMap<>();
        data.put("moveId", move.getMoveId().toString());
        data.put("status", move.getStatus().toString());


        if (move.getDriver() != null) {
            Driver driver = move.getDriver();
            com.heim.api.users.domain.entity.User user = driver.getUser();

            data.put("driverLat", String.valueOf(driverLocation.getLatitude()));
            data.put("driverLng", String.valueOf(driverLocation.getLongitude()));

            // Datos del vehículo
            //data.put("enrollVehicle", driver.getEnrollVehicle());
         //   data.put("vehicleType", driver.getVehicleType());

            // Datos personales del usuario que es conductor
            data.put("driverName", user.getFullName());
            data.put("driverPhone", user.getPhone());
            data.put("driverImageUrl", user.getUrlAvatarProfile());

        }

        return data;
    }

    public List<MovingHistoryDTO> getMovingHistoryByDriverId(Long driverId){
        List<Move> moves = moveRepository.findByDriverIdAndStatus(driverId,MoveStatus.MOVE_COMPLETE);
        logger.info("MUDANZAS {}", moves);
            return movingHistoryMapper.toDtoList(moves);
    }

    public List<MovingHistoryDTO> getMovingHistoryByUserId(Long userId){
        List<Move> moves = moveRepository.findByUser_UserIdAndStatus(userId, MoveStatus.MOVE_COMPLETE);
        return movingHistoryMapper.toDtoList(moves);
    }


  public MoveSummaryDTO movingSummary(Long moveId){
        Optional<Move> moveOptional = moveRepository.findById(moveId);
        if (moveOptional.isPresent()){
            Move move = moveOptional.get();
            logger.info("DATA MOVING {}",moveOptional);
            return  MoveSummaryMapper.toSummaryDTO(move);
        }else {
            throw new NotFoundException("Mudanza con ID " + moveId + " no encontrada.");
        }
  }

    public MoveDetailsDTO  findMoveDetails(Long moveId){
        Move move = moveRepository.findById(moveId)
                .orElseThrow(() -> new NotFoundException("Mudanza no encontrada"));

        // 2. Crear el DTO y llenarlo con datos de la mudanza
        MoveDetailsDTO dto = new MoveDetailsDTO();
        dto.setMoveId(move.getMoveId());
        dto.setOrigin(move.getOrigin());
        dto.setDestination(move.getDestination());
        dto.setAmount(move.getPrice());
        dto.setPaymentMethod(move.getPaymentMethod());
        dto.setTypeOfMove(move.getTypeOfMove());
        dto.setMovingDate(move.getEndTime());

        // 3. Usar el driverId de la mudanza para obtener los datos del conductor
        driverRepository.findById(move.getDriver().getId()).ifPresent(driver -> {
            dto.setDriverName(driver.getUser().getFullName());
            dto.setTypeOfVehicle(driver.getVehicleType());

        });

        return dto;
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



    private void createPendingEarning(Move move, BigDecimal commissionRate) {
        // Calcular el monto neto para el conductor
        BigDecimal driverShare = BigDecimal.ONE.subtract(commissionRate);
        BigDecimal netAmount = move.getPrice().multiply(driverShare);

        Earning earning = new Earning();
        earning.setMove(move);
        earning.setDriver(move.getDriver());
        earning.setNetAmount(netAmount);
        earning.setSettled(false); // 🚨 ESTADO: Ganancia Pendiente

        earningRepository.save(earning);
        logger.info("Ganancia Pendiente creada para Conductor {}: Monto Neto {}", move.getUser().getDriver(), netAmount);
    }

    }
