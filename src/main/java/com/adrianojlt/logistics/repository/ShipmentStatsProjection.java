package com.adrianojlt.logistics.repository;

import java.math.BigDecimal;

public interface ShipmentStatsProjection {
    long getTotalShipments();
    BigDecimal getTotalIncome();
    BigDecimal getTotalCosts();
    BigDecimal getNetProfitOrLoss();
    long getProfitableCount();
    long getLossCount();
    long getBreakEvenCount();
    BigDecimal getAverageMargin();
}
