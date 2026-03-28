import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class ShipmentService {
  private readonly baseUrl = 'http://localhost:8080/api/shipments';

  constructor(private http: HttpClient) {}

  calculate(payload: { income: number; cost: number; additionalCost: number }): Observable<any> {
    return this.http.post<{ data: any }>(`${this.baseUrl}/calculate`, payload).pipe(
      map((res) => res.data)
    );
  }

  findAll(): Observable<any> {
    return this.http.get<{ data: any[] }>(this.baseUrl).pipe(
      map((res) => res.data)
    );
  }
}
