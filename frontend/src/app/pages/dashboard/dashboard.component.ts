import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { NgxChartsModule } from '@swimlane/ngx-charts';

import { ShipmentService, CarrierStats } from '../../services/shipment.service';
import { ShipmentStatsModel } from '../../models/shipment-stats.model';
import { ShipmentModel } from '../../models/shipment.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    NgxChartsModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {

  stats: ShipmentStatsModel | null = null;
  loading = true;
  isEmpty = false;

  barChartData: { name: string; series: { name: string; value: number }[] }[] = [];
  donutData: { name: string; value: number }[] = [];
  lineChartData: { name: string; series: { name: Date; value: number }[] }[] = [];
  carrierChartData: { name: string; value: number }[] = [];

  readonly barColorScheme: any = { domain: ['#1976D2', '#F57C00'] };
  readonly donutColorScheme: any = { domain: ['#388E3C', '#D32F2F', '#757575'] };
  readonly lineColorScheme: any = { domain: ['#7B1FA2'] };
  readonly lineReferenceLines = [{ name: 'Break-Even', value: 0 }];
  readonly carrierColorScheme: any = { domain: ['#1976D2', '#F57C00', '#7B1FA2', '#388E3C'] };

  constructor(
    private shipmentService: ShipmentService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    forkJoin({
      stats: this.shipmentService.getStats(),
      shipments: this.shipmentService.findAll(),
      carrierStats: this.shipmentService.getStatsByCarrier(),
    }).subscribe({
      next: ({ stats, shipments, carrierStats }) => {
        this.stats = stats;
        this.isEmpty = stats.totalShipments === 0;

        if (!this.isEmpty) {
          this.buildCharts(stats, shipments, carrierStats);
        }

        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  goToCalculateProfit(): void {
    this.router.navigate(['/calculate-profit']);
  }

  private buildCharts(stats: ShipmentStatsModel, shipments: ShipmentModel[], carrierStats: CarrierStats[]): void {
    this.barChartData = shipments.map(s => ({
      name: `#${s.id}`,
      series: [
        { name: 'Income', value: s.income },
        { name: 'Total Costs', value: s.totalCosts },
      ],
    }));

    this.donutData = [
      { name: 'Profitable', value: stats.profitableCount },
      { name: 'Loss', value: stats.lossCount },
      { name: 'Break-Even', value: stats.breakEvenCount },
    ];

    this.lineChartData = [{
      name: 'Profit / Loss',
      series: shipments.map(s => ({
        name: new Date(s.createdAt),
        value: s.profitOrLoss,
      })),
    }];

    this.carrierChartData = carrierStats.map(s => ({
      name: s.carrier,
      value: s.averageMargin,
    }));
  }
}
