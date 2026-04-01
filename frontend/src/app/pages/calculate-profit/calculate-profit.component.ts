import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { ShipmentService } from '../../services/shipment.service';
import { ShipmentModel } from '../../models/shipment.model';

type FilterMode = 'all' | 'profits' | 'losses';

@Component({
  selector: 'app-calculate-profit',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
    MatSortModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonToggleModule,
    MatIconModule,
    MatSelectModule,
    MatTooltipModule,
    MatAutocompleteModule,
  ],
  templateUrl: './calculate-profit.component.html',
  styleUrl: './calculate-profit.component.css',
})
export class CalculateProfitComponent implements OnInit, AfterViewInit {

  @ViewChild(MatSort) sort!: MatSort;

  form: FormGroup;
  lastShipmentId: number | null = null;
  dataSource = new MatTableDataSource<ShipmentModel>();
  displayedColumns = ['id', 'origin', 'destination', 'carrier', 'income', 'totalCosts', 'profitOrLoss', 'profitMargin', 'createdAt'];
  loading = false;
  errorMessage = '';

  startDate: Date | null = null;
  endDate: Date | null = null;
  filterMode: FilterMode = 'all';
  filterCarriers: string[] = [];
  selectedCarrier: string | null = null;

  readonly cities: string[] = [
    'Lisbon', 'Porto', 'Faro', 'Coimbra', 'Braga',
    'Madrid', 'Paris', 'London', 'Munich', 'Berlin',
    'Amsterdam', 'Rome', 'Vienna', 'Brussels', 'Warsaw',
  ];

  readonly carriers: string[] = ['DHL', 'DPD', 'FedEx', 'UPS', 'TNT', 'Internal'];

  filteredOrigins: string[] = [];
  filteredDestinations: string[] = [];

  constructor(private fb: FormBuilder, private shipmentService: ShipmentService) {
    this.form = this.fb.group({
      income: [null, [Validators.required, Validators.min(0)]],
      cost: [null, [Validators.required, Validators.min(0)]],
      additionalCost: [null],
      origin: ['', [Validators.required, Validators.maxLength(100)]],
      destination: ['', [Validators.required, Validators.maxLength(100)]],
      carrier: [null],
    });
  }

  ngOnInit(): void {
    this.filteredOrigins = [...this.cities];
    this.filteredDestinations = [...this.cities];

    this.form.get('origin')!.valueChanges.subscribe(val => {
      this.filteredOrigins = this.filterCities(val);
    });

    this.form.get('destination')!.valueChanges.subscribe(val => {
      this.filteredDestinations = this.filterCities(val);
    });

    this.loadShipments();
    this.shipmentService.getCarriers().subscribe({
      next: (carriers) => (this.filterCarriers = carriers),
    });
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
  }

  filterCities(value: string): string[] {
    const filter = value?.toLowerCase() ?? '';
    return this.cities.filter(c => c.toLowerCase().includes(filter));
  }

  loadShipments(): void {
    const startDate = this.startDate ? this.startDate.toISOString().slice(0, 19) : undefined;
    const endDate = this.endDate ? this.endDate.toISOString().slice(0, 19) : undefined;
    const onlyLosses = this.filterMode === 'losses';
    const onlyProfits = this.filterMode === 'profits';
    const carrier = this.selectedCarrier ?? undefined;

    this.shipmentService.findAll(startDate, endDate, onlyLosses, onlyProfits, carrier).subscribe({
      next: (shipments: ShipmentModel[]) => {
        this.dataSource.data = shipments;
      },
      error: () => (this.errorMessage = 'Failed to load shipments.'),
    });
  }

  applyFilters(): void {
    this.loadShipments();
  }

  clearFilters(): void {
    this.startDate = null;
    this.endDate = null;
    this.filterMode = 'all';
    this.selectedCarrier = null;
    this.loadShipments();
  }

  onCalculate(): void {
    if (this.form.invalid) return;

    this.loading = true;
    this.errorMessage = '';
    const { income, cost, additionalCost, origin, destination, carrier } = this.form.value;
    const payload = { income, cost, additionalCost: additionalCost ?? 0, origin, destination, carrier: carrier || null };

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

  getMarginBadgeClass(margin: number): string {
    if (margin > 0) return 'badge-profit';
    if (margin < 0) return 'badge-loss';
    return 'badge-neutral';
  }

  formatMargin(margin: number): string {
    return (margin > 0 ? '+' : '') + margin.toFixed(2) + '%';
  }

  exportToCsv(): void {
    const headers = ['ID', 'Origin', 'Destination', 'Carrier', 'Income', 'Total Costs', 'Profit or Loss', 'Margin %', 'Created At'];
    const rows = this.dataSource.data.map(row => [
      row.id,
      row.origin,
      row.destination,
      row.carrier ?? '—',
      row.income,
      row.totalCosts,
      row.profitOrLoss,
      row.profitMargin + '%',
      row.createdAt,
    ]);

    const csvContent = [headers, ...rows]
      .map(row => row.join(','))
      .join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `shipment-calculations-${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
