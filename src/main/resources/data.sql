INSERT IGNORE INTO users (username, password) VALUES
    ('adriano', '$2a$10$o8eEJ.i2U1LzX0Ys0pjroeIqRl/sRKewlQvkZnzLV.MdCQQUoWAPa'),
    ('dachser', '$2a$10$NGEzy3scgS1L5nfq.x3o8.H5ChneJvtysDoJbMI0nzWMQ9KMkRMs.');

INSERT INTO shipment_calculation (income, cost, additional_cost, total_costs, profit_or_loss, profit_margin)
SELECT * FROM (
    SELECT 1000.00, 150.00, 50.00,  200.00,  800.00,  80.00
    UNION ALL
    SELECT 500.00,  380.00, 20.00,  400.00,  100.00,  20.00
    UNION ALL
    SELECT 300.00,  270.00, 30.00,  300.00,    0.00,   0.00
    UNION ALL
    SELECT 500.00,  550.00, 50.00,  600.00, -100.00, -20.00
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM shipment_calculation);
