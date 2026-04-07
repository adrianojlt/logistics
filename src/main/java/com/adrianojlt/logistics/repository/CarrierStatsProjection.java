package com.adrianojlt.logistics.repository;

public interface CarrierStatsProjection {
    String getCarrier();
    Double getAverageMargin();
    Long getShipmentCount();
}
