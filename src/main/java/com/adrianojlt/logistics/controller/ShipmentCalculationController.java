package com.adrianojlt.logistics.controller;

import com.adrianojlt.logistics.dto.ApiResponse;
import com.adrianojlt.logistics.dto.CarrierStatsDTO;
import com.adrianojlt.logistics.dto.ShipmentCalculationRequestDTO;
import com.adrianojlt.logistics.dto.ShipmentCalculationResponseDTO;
import com.adrianojlt.logistics.dto.ShipmentStatsDTO;
import com.adrianojlt.logistics.service.ShipmentCalculationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentCalculationController {

    private final ShipmentCalculationService service;

    public ShipmentCalculationController(ShipmentCalculationService service) {
        this.service = service;
    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<ShipmentCalculationResponseDTO>> calculate(
            @Valid @RequestBody ShipmentCalculationRequestDTO request,
            Principal principal) {
        ShipmentCalculationResponseDTO result = service.calculate(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ShipmentStatsDTO>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(service.getStats()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShipmentCalculationResponseDTO>>> findAll(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "false") boolean onlyLosses,
            @RequestParam(defaultValue = "false") boolean onlyProfits,
            @RequestParam(required = false) String carrier) {

        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;

        List<ShipmentCalculationResponseDTO> results = service.findAll(start, end, onlyLosses, onlyProfits, carrier);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/carriers")
    public ResponseEntity<ApiResponse<List<String>>> getCarriers() {
        return ResponseEntity.ok(ApiResponse.ok(service.getDistinctCarriers()));
    }

    @GetMapping("/stats/by-carrier")
    public ResponseEntity<ApiResponse<List<CarrierStatsDTO>>> getStatsByCarrier() {
        return ResponseEntity.ok(ApiResponse.ok(service.getStatsByCarrier()));
    }
}
