package com.adrianojlt.logistics.service;

import com.adrianojlt.logistics.dto.CarrierStatsDTO;
import com.adrianojlt.logistics.dto.ShipmentCalculationRequestDTO;
import com.adrianojlt.logistics.dto.ShipmentCalculationResponseDTO;
import com.adrianojlt.logistics.dto.ShipmentStatsDTO;
import com.adrianojlt.logistics.entity.ShipmentCalculation;
import com.adrianojlt.logistics.mapper.ShipmentCalculationMapper;
import com.adrianojlt.logistics.repository.CarrierStatsProjection;
import com.adrianojlt.logistics.repository.ShipmentCalculationRepository;
import com.adrianojlt.logistics.repository.ShipmentStatsProjection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ShipmentCalculationService {

    private final ShipmentCalculationMapper mapper;
    private final ShipmentCalculationRepository repository;

    public ShipmentCalculationService(
            ShipmentCalculationMapper mapper,
            ShipmentCalculationRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Transactional
    public ShipmentCalculationResponseDTO calculate(ShipmentCalculationRequestDTO request, String createdBy) {

        BigDecimal additionalCost = request.getAdditionalCost() != null
                ? request.getAdditionalCost()
                : BigDecimal.ZERO;

        BigDecimal totalCosts = request.getCost().add(additionalCost);
        BigDecimal profitOrLoss = request.getIncome().subtract(totalCosts);

        BigDecimal profitMargin = request.getIncome().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : profitOrLoss.divide(request.getIncome(), 4, RoundingMode.HALF_UP)
                              .multiply(new BigDecimal("100"))
                              .setScale(2, RoundingMode.HALF_UP);

        ShipmentCalculation entity = mapper.toEntity(request);

        entity.setAdditionalCost(additionalCost);
        entity.setTotalCosts(totalCosts);
        entity.setProfitOrLoss(profitOrLoss);
        entity.setProfitMargin(profitMargin);
        entity.setCreatedBy(createdBy);

        ShipmentCalculation saved = repository.save(entity);

        log.info("calculation_saved user={} income={} totalCosts={} profitOrLoss={} profitMargin={}",
                createdBy, request.getIncome(), totalCosts, profitOrLoss, profitMargin);

        return mapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public ShipmentStatsDTO getStats() {
        ShipmentStatsProjection p = repository.findStats();
        return ShipmentStatsDTO.builder()
                .totalShipments(p.getTotalShipments())
                .totalIncome(p.getTotalIncome())
                .totalCosts(p.getTotalCosts())
                .netProfitOrLoss(p.getNetProfitOrLoss())
                .profitableCount(p.getProfitableCount())
                .lossCount(p.getLossCount())
                .breakEvenCount(p.getBreakEvenCount())
                .averageMargin(p.getAverageMargin().setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    @Transactional(readOnly = true)
    public List<ShipmentCalculationResponseDTO> findAll(
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean onlyLosses,
            boolean onlyProfits,
            String carrier) {

        return repository.findWithFilters(startDate, endDate, onlyLosses, onlyProfits, carrier)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctCarriers() {
        return repository.findDistinctCarriers();
    }

    @Transactional(readOnly = true)
    public List<CarrierStatsDTO> getStatsByCarrier() {
        return repository.findStatsByCarrier().stream()
                .map(p -> new CarrierStatsDTO(p.getCarrier(), p.getAverageMargin(), p.getShipmentCount()))
                .toList();
    }
}
