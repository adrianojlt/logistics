package com.adrianojlt.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentStatsDTO {

    private long totalShipments;
    private BigDecimal totalIncome;
    private BigDecimal totalCosts;
    private BigDecimal netProfitOrLoss;
    private long profitableCount;
    private long lossCount;
    private long breakEvenCount;
    private BigDecimal averageMargin;
}
