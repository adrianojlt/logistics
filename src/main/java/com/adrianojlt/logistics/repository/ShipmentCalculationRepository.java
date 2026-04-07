package com.adrianojlt.logistics.repository;

import com.adrianojlt.logistics.entity.ShipmentCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShipmentCalculationRepository extends JpaRepository<ShipmentCalculation, Long> {

    @Query("SELECT " +
           "COUNT(s) as totalShipments, " +
           "COALESCE(SUM(s.income), 0) as totalIncome, " +
           "COALESCE(SUM(s.totalCosts), 0) as totalCosts, " +
           "COALESCE(SUM(s.profitOrLoss), 0) as netProfitOrLoss, " +
           "COUNT(CASE WHEN s.profitOrLoss > 0 THEN 1 END) as profitableCount, " +
           "COUNT(CASE WHEN s.profitOrLoss < 0 THEN 1 END) as lossCount, " +
           "COUNT(CASE WHEN s.profitOrLoss = 0 THEN 1 END) as breakEvenCount, " +
           "COALESCE(AVG(s.profitMargin), 0) as averageMargin " +
           "FROM ShipmentCalculation s")
    ShipmentStatsProjection findStats();

    @Query("SELECT s FROM ShipmentCalculation s WHERE " +
           "(:startDate IS NULL OR s.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR s.createdAt <= :endDate) AND " +
           "(:onlyLosses = false OR s.profitOrLoss < 0) AND " +
           "(:onlyProfits = false OR s.profitOrLoss > 0) AND " +
           "(:carrier IS NULL OR s.carrier = :carrier) " +
           "ORDER BY s.createdAt DESC")
    List<ShipmentCalculation> findWithFilters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("onlyLosses") boolean onlyLosses,
            @Param("onlyProfits") boolean onlyProfits,
            @Param("carrier") String carrier
    );

    @Query("SELECT DISTINCT s.carrier FROM ShipmentCalculation s WHERE s.carrier IS NOT NULL ORDER BY s.carrier")
    List<String> findDistinctCarriers();

    @Query("SELECT s.carrier as carrier, AVG(s.profitMargin) as averageMargin, COUNT(s) as shipmentCount " +
           "FROM ShipmentCalculation s " +
           "WHERE s.carrier IS NOT NULL " +
           "GROUP BY s.carrier " +
           "ORDER BY AVG(s.profitMargin) DESC")
    List<CarrierStatsProjection> findStatsByCarrier();
}
