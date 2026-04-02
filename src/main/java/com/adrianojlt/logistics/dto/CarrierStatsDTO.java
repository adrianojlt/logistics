package com.adrianojlt.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierStatsDTO {
    private String carrier;
    private Double averageMargin;
    private long shipmentCount;
}
