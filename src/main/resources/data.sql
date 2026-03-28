-- income > costs
INSERT INTO shipment_calculation (income, cost, additional_cost, total_costs, profit_or_loss)
VALUES (5000.00, 3000.00, 200.00, 3200.00, 1800.00);

-- small margin
INSERT INTO shipment_calculation (income, cost, additional_cost, total_costs, profit_or_loss)
VALUES (2500.00, 2000.00, 0.00, 2000.00, 500.00);

-- income < costs
INSERT INTO shipment_calculation (income, cost, additional_cost, total_costs, profit_or_loss)
VALUES (1800.00, 2200.00, 150.00, 2350.00, -550.00);

-- large deficit
INSERT INTO shipment_calculation (income, cost, additional_cost, total_costs, profit_or_loss)
VALUES (3000.00, 4000.00, 500.00, 4500.00, -1500.00);
