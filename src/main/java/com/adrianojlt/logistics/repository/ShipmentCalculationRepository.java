package com.adrianojlt.logistics.repository;

import com.adrianojlt.logistics.entity.ShipmentCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShipmentCalculationRepository extends JpaRepository<ShipmentCalculation, Long> {

    @Query("SELECT s FROM ShipmentCalculation s WHERE " +
           "(:startDate IS NULL OR s.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR s.createdAt <= :endDate) AND " +
           "(:onlyLosses = false OR s.profitOrLoss < 0) AND " +
           "(:onlyProfits = false OR s.profitOrLoss > 0) " +
           "ORDER BY s.createdAt DESC")
    List<ShipmentCalculation> findWithFilters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("onlyLosses") boolean onlyLosses,
            @Param("onlyProfits") boolean onlyProfits
    );
}
