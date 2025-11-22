package com.heim.api.drivers.infraestructure.controller;

import com.heim.api.drivers.application.dto.*;
import com.heim.api.drivers.application.service.DriverService;
import com.heim.api.hazelcast.service.HazelcastGeoService;
import com.heim.api.move.application.service.MoveService;
import com.heim.api.move.domain.entity.Move;
import com.heim.api.users.infraestructure.exceptions.EmailAlreadyRegisteredException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/drivers")
@CrossOrigin("*")
public class DriverController {
    private static final Logger logger = LoggerFactory.getLogger(DriverController.class);
    private final DriverService driverService;
    private final MoveService tripService;


    @Autowired
    public  DriverController(DriverService driverService,
                             HazelcastGeoService hazelcastGeoService,
                             MoveService tripService){

        this.driverService = driverService;
        this.tripService  = tripService;
    }

    private static final String SUCCESS = "success";
    private static final String STATUS_UPDATED = "Estado actualizado correctamente";
    private static final String DISCONNECTED_SUCCESS = "Te has desconectado satisfactoriamente";

    @PostMapping("register")
    public ResponseEntity<?> registerDriver(@RequestBody DriverRequest driverRequest){
        try{
            DriverResponse registerDriver = driverService.registerDriver(driverRequest);
            return  ResponseEntity.ok(registerDriver);
        } catch (EmailAlreadyRegisteredException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e){
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Ocurrió un error inesperado. Nuestros desarrolladores ya fueron informados.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable Long driverId) {
        try {
            // Obtener el usuario usando el servicio
            DriverResponse driverResponse = driverService.getDriverById(driverId);
            return new ResponseEntity<>(driverResponse, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            // El usuario no se encuentra
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Error interno
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{driverId}")
    public ResponseEntity<DriverResponse> updatedDriverData(@PathVariable Long driverId, @RequestBody DriverRequest driverRequest) {
        try {
            if (driverRequest == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Mala solicitud si los datos están vacíos
            }
            // Llamada al servicio para actualizar el usuario
            DriverResponse updatedDriverData = driverService.updatedDriverData(driverId, driverRequest);

            return new ResponseEntity<>(updatedDriverData, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            // Si el usuario no se encuentra
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            // En caso de que haya algún error con los datos (por ejemplo, email duplicado)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Error interno
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{driverId}")
    public ResponseEntity<String> driverDelete(@PathVariable Long driverId) {
        try {
            driverService.driverDelete(driverId);
            return ResponseEntity.ok("Usuario eliminado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Se produjo un error al procesar la solicitud");
        }
    }

    @PutMapping("/{driverId}/connect")
    public ResponseEntity<Map<String, String>> updateStatus(@PathVariable Long driverId,
                                                             @RequestBody DriverStatusRequest request) {
        driverService.connectDriver(driverId, request);
        logger.info("🔄 Estado actualizado para el conductor {}", driverId);
        return ResponseEntity.ok(buildResponse(SUCCESS, STATUS_UPDATED));
    }

    @PutMapping("/location/{driverId}")
    public ResponseEntity<Map<String, String>> updateDriverLocation(@RequestBody DriverUpdateLocationRequest request,@PathVariable Long driverId) {
        driverService.updateDriverLocation(request, driverId);
        logger.info("📍 Ubicación actualizada para el conductor {}", driverId);
        return ResponseEntity.ok(buildResponse(SUCCESS, "Ubicación actualizada correctamente"));
    }


    @PutMapping("/{driverId}/disconnected")
    public ResponseEntity<Map<String, String>> disconnectedStatus(@PathVariable Long driverId,
                                                                  @Valid  @RequestBody DriverStatusDisconnectedRequest request) {
        driverService.driverDisconnected(driverId, request);
        logger.info("🔴 Conductor {} desconectado", driverId);

        return ResponseEntity.ok(buildResponse(SUCCESS, DISCONNECTED_SUCCESS));
    }

    @GetMapping("/{driverId}/get/status")
    public ResponseEntity<DriverStatusResponse> getDriverStatus(@PathVariable Long driverId) {
        DriverStatusResponse response = driverService.getDriverStatus(driverId);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/driver/trip/{tripId}")
    public ResponseEntity<Move> getTripForDriver(@PathVariable Long tripId, @RequestParam Long driverId ){
        Optional<Move> tripOptional = tripService.getTripForDriver(tripId, driverId);
        return tripOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    private Map<String, String> buildResponse(String status, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        return response;
    }

}
