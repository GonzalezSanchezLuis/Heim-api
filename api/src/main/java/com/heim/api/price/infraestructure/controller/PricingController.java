package com.heim.api.price.infraestructure.controller;

import com.heim.api.price.application.dto.PriceRequest;
import com.heim.api.price.application.dto.PriceResponse;
import com.heim.api.price.service.PriceService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/price/")
@CrossOrigin("*")
public class PricingController {

    private static final Logger logger = LoggerFactory.getLogger(PricingController.class);
    private final PriceService priceService;


    @Autowired
    public PricingController(PriceService priceService) {
        this.priceService = priceService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<?> getPrice(@RequestBody PriceRequest priceRequest) {
        try {
            logger.info("Iniciando cálculo para tipo: {}", priceRequest.getTypeOfMove());

            PriceResponse response = priceService.calculatePrice(priceRequest);

            logger.info("Cálculo finalizado. Monto: {}", response.getPrice());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al calcular precio: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "No se pudo calcular el precio"));
        }
    }
}

