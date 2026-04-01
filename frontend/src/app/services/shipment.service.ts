import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { ShipmentModel } from '../models/shipment.model';
import { ShipmentStatsModel } from '../models/shipment-stats.model';

export interface CarrierStats {
  carrier: string;
  averageMargin: number;
  shipmentCount: number;
}

@Injectable({ providedIn: 'root' })
export class ShipmentService {
  private readonly baseUrl = `${environment.apiUrl}/api/shipments`;

  constructor(private http: HttpClient) {}

  getStats(): Observable<ShipmentStatsModel> {
    return this.http.get<{ data: ShipmentStatsModel }>(`${this.baseUrl}/stats`).pipe(
      map((res) => res.data)
    );
  }

  calculate(payload: {
    income: number;
    cost: number;
    additionalCost: number;
    origin: string;
    destination: string;
    carrier: string | null;
  }): Observable<ShipmentModel> {
    return this.http.post<{ data: ShipmentModel }>(`${this.baseUrl}/calculate`, payload).pipe(
      map((res) => res.data)
    );
  }

  findAll(
    startDate?: string,
    endDate?: string,
    onlyLosses = false,
    onlyProfits = false,
    carrier?: string
  ): Observable<ShipmentModel[]> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    if (onlyLosses) params = params.set('onlyLosses', 'true');
    if (onlyProfits) params = params.set('onlyProfits', 'true');
    if (carrier) params = params.set('carrier', carrier);

    return this.http.get<{ data: ShipmentModel[] }>(this.baseUrl, { params }).pipe(
      map((res) => res.data)
    );
  }

  getCarriers(): Observable<string[]> {
    return this.http.get<{ data: string[] }>(`${this.baseUrl}/carriers`).pipe(
      map((res) => res.data)
    );
  }

  getStatsByCarrier(): Observable<CarrierStats[]> {
    return this.http.get<{ data: CarrierStats[] }>(`${this.baseUrl}/stats/by-carrier`).pipe(
      map((res) => res.data)
    );
  }
}
