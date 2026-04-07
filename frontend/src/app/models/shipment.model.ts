export interface ShipmentModel {
  id: number;
  income: number;
  cost: number;
  additionalCost: number;
  totalCosts: number;
  profitOrLoss: number;
  profitMargin: number;
  origin: string;
  destination: string;
  carrier: string | null;
  createdAt: string;
}
