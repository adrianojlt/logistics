package com.adrianojlt.logistics.service;

import com.adrianojlt.logistics.dto.ShipmentCalculationRequestDTO;
import com.adrianojlt.logistics.dto.ShipmentCalculationResponseDTO;
import com.adrianojlt.logistics.entity.ShipmentCalculation;
import com.adrianojlt.logistics.mapper.ShipmentCalculationMapper;
import com.adrianojlt.logistics.repository.ShipmentCalculationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
    public ShipmentCalculationResponseDTO calculate(ShipmentCalculationRequestDTO request) {

        BigDecimal additionalCost = request.getAdditionalCost() != null
                ? request.getAdditionalCost()
                : BigDecimal.ZERO;

        BigDecimal totalCosts = request.getCost().add(additionalCost);
        BigDecimal profitOrLoss = request.getIncome().subtract(totalCosts);

        ShipmentCalculation entity = mapper.toEntity(request);

        entity.setAdditionalCost(additionalCost);
        entity.setTotalCosts(totalCosts);
        entity.setProfitOrLoss(profitOrLoss);

        ShipmentCalculation saved = repository.save(entity);

        return mapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ShipmentCalculationResponseDTO> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }
}
