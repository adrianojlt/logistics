package com.adrianojlt.logistics.controller;

import com.adrianojlt.logistics.dto.ApiResponse;
import com.adrianojlt.logistics.dto.ShipmentCalculationRequestDTO;
import com.adrianojlt.logistics.dto.ShipmentCalculationResponseDTO;
import com.adrianojlt.logistics.service.ShipmentCalculationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/shipments")
public class ShipmentCalculationController {

    private final ShipmentCalculationService service;

    public ShipmentCalculationController(ShipmentCalculationService service) {
        this.service = service;
    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<ShipmentCalculationResponseDTO>> calculate(
            @Valid @RequestBody ShipmentCalculationRequestDTO request) {
        ShipmentCalculationResponseDTO result = service.calculate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShipmentCalculationResponseDTO>>> findAll() {
        List<ShipmentCalculationResponseDTO> results = service.findAll();
        return ResponseEntity.ok(ApiResponse.ok(results));
    }
}
