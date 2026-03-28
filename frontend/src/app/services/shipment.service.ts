import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ShipmentService {
  constructor(private http: HttpClient) {}
}
