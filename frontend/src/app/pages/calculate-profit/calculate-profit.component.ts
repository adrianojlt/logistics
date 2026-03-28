import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { ShipmentService } from '../../services/shipment.service';
import { ShipmentModel } from '../../models/shipment.model';

@Component({
  selector: 'app-calculate-profit',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
  ],
  templateUrl: './calculate-profit.component.html',
  styleUrl: './calculate-profit.component.css',
})
export class CalculateProfitComponent implements OnInit {
  form: FormGroup;
  lastShipmentId: number | null = null;
  shipments: ShipmentModel[] = [];
  displayedColumns = ['id', 'income', 'totalCosts', 'profitOrLoss'];
  loading = false;
  errorMessage = '';

  constructor(private fb: FormBuilder, private shipmentService: ShipmentService) {
    this.form = this.fb.group({
      income: [null, [Validators.required, Validators.min(0)]],
      cost: [null, [Validators.required, Validators.min(0)]],
      additionalCost: [null],
    });
  }

  ngOnInit(): void {
    this.loadShipments();
  }

  loadShipments(): void {
    this.shipmentService.findAll().subscribe({
      next: (data: ShipmentModel[]) => (this.shipments = data),
      error: () => (this.errorMessage = 'Failed to load shipments.'),
    });
  }

  onCalculate(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.errorMessage = '';
    const { income, cost, additionalCost } = this.form.value;
    const payload = { income, cost, additionalCost: additionalCost ?? 0 };
    this.shipmentService.calculate(payload).subscribe({
      next: (result: ShipmentModel) => {
        this.lastShipmentId = result.id;
        this.form.reset();
        this.loadShipments();
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Calculation failed. Please try again.';
        this.loading = false;
      },
    });
  }
}
