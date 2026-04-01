package com.adrianojlt.logistics.repository;

import com.adrianojlt.logistics.dto.CarrierStatsDTO;
import com.adrianojlt.logistics.entity.ShipmentCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ShipmentCalculationRepository extends JpaRepository<ShipmentCalculation, Long> {

    @Query("SELECT COUNT(s) FROM ShipmentCalculation s")
    long countAll();

    @Query("SELECT COALESCE(SUM(s.income), 0) FROM ShipmentCalculation s")
    BigDecimal sumIncome();

    @Query("SELECT COALESCE(SUM(s.totalCosts), 0) FROM ShipmentCalculation s")
    BigDecimal sumTotalCosts();

    @Query("SELECT COALESCE(SUM(s.profitOrLoss), 0) FROM ShipmentCalculation s")
    BigDecimal sumProfitOrLoss();

    @Query("SELECT COUNT(s) FROM ShipmentCalculation s WHERE s.profitOrLoss > 0")
    long countProfitable();

    @Query("SELECT COUNT(s) FROM ShipmentCalculation s WHERE s.profitOrLoss < 0")
    long countLoss();

    @Query("SELECT COUNT(s) FROM ShipmentCalculation s WHERE s.profitOrLoss = 0")
    long countBreakEven();

    @Query("SELECT COALESCE(AVG(s.profitMargin), 0) FROM ShipmentCalculation s")
    BigDecimal averageMargin();

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

    @Query("SELECT new com.adrianojlt.logistics.dto.CarrierStatsDTO(" +
           "s.carrier, AVG(s.profitMargin), COUNT(s)) " +
           "FROM ShipmentCalculation s " +
           "WHERE s.carrier IS NOT NULL " +
           "GROUP BY s.carrier " +
           "ORDER BY AVG(s.profitMargin) DESC")
    List<CarrierStatsDTO> findStatsByCarrier();
}
