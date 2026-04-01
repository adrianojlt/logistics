package com.adrianojlt.logistics.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCalculationRequestDTO {

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal cost;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal income;

    @DecimalMin("0.0")
    private BigDecimal additionalCost;

    @NotBlank(message = "Origin is required")
    @Size(max = 100)
    private String origin;

    @NotBlank(message = "Destination is required")
    @Size(max = 100)
    private String destination;

    @Size(max = 100)
    private String carrier;
}
