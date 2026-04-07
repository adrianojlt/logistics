INSERT IGNORE INTO users (username, password) VALUES
    ('adriano', '$2a$10$o8eEJ.i2U1LzX0Ys0pjroeIqRl/sRKewlQvkZnzLV.MdCQQUoWAPa'),
    ('dachser', '$2a$10$NGEzy3scgS1L5nfq.x3o8.H5ChneJvtysDoJbMI0nzWMQ9KMkRMs.');

INSERT INTO shipment_calculation
  (income, cost, additional_cost, total_costs, profit_or_loss, profit_margin, origin, destination, carrier, created_by, created_at)
SELECT * FROM (
    SELECT 1000.00, 150.00,  50.00,  200.00,  800.00,  80.00, 'Lisbon',  'Madrid',    'Internal', 'system', DATEADD('DAY', -7, CURRENT_TIMESTAMP)
    UNION ALL
    SELECT  500.00, 380.00,  20.00,  400.00,  100.00,  20.00, 'Porto',   'Paris',     'Internal', 'system', DATEADD('DAY', -6, CURRENT_TIMESTAMP)
    UNION ALL
    SELECT  300.00, 270.00,  30.00,  300.00,    0.00,   0.00, 'Lisbon',  'London',    'Internal', 'system', DATEADD('DAY', -5, CURRENT_TIMESTAMP)
    UNION ALL
    SELECT  500.00, 550.00,  50.00,  600.00, -100.00, -20.00, 'Faro',    'Berlin',    'Internal', 'system', DATEADD('DAY', -4, CURRENT_TIMESTAMP)
    UNION ALL
    SELECT 1200.00, 900.00,  60.00,  960.00,  240.00,  20.00, 'Lisbon',  'Munich',    'DHL',      'system', DATEADD('DAY', -3, CURRENT_TIMESTAMP)
    UNION ALL
    SELECT  800.00, 200.00,  50.00,  250.00,  550.00,  68.75, 'Porto',   'Amsterdam', 'DPD',      'system', DATEADD('DAY', -2, CURRENT_TIMESTAMP)
    UNION ALL
    SELECT  600.00, 520.00,  80.00,  600.00,    0.00,   0.00, 'Lisbon',  'Rome',      'FedEx',    'system', DATEADD('DAY', -1, CURRENT_TIMESTAMP)
    UNION ALL
    SELECT  750.00, 700.00, 100.00,  800.00,  -50.00,  -6.67, 'Coimbra', 'Vienna',    'DHL',      'system', CURRENT_TIMESTAMP
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM shipment_calculation);
