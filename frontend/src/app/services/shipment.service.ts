import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { ShipmentModel } from '../models/shipment.model';

@Injectable({ providedIn: 'root' })
export class ShipmentService {
  private readonly baseUrl = `${environment.apiUrl}/api/shipments`;

  constructor(private http: HttpClient) {}

  calculate(payload: { income: number; cost: number; additionalCost: number }): Observable<ShipmentModel> {
    return this.http.post<{ data: ShipmentModel }>(`${this.baseUrl}/calculate`, payload).pipe(
      map((res) => res.data)
    );
  }

  findAll(
    startDate?: string,
    endDate?: string,
    onlyLosses = false,
    onlyProfits = false
  ): Observable<ShipmentModel[]> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    if (onlyLosses) params = params.set('onlyLosses', 'true');
    if (onlyProfits) params = params.set('onlyProfits', 'true');

    return this.http.get<{ data: ShipmentModel[] }>(this.baseUrl, { params }).pipe(
      map((res) => res.data)
    );
  }
}
