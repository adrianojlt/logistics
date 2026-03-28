CREATE TABLE IF NOT EXISTS shipment_calculation (
    id               BIGINT          AUTO_INCREMENT PRIMARY KEY,
    income           DECIMAL(10, 2)  NOT NULL,
    cost             DECIMAL(10, 2)  NOT NULL,
    additional_cost  DECIMAL(10, 2)  NOT NULL DEFAULT 0,
    total_costs      DECIMAL(10, 2)  NOT NULL,
    profit_or_loss   DECIMAL(10, 2)  NOT NULL,
    created_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);
