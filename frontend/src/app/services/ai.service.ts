import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AiService {

  private readonly apiUrl = `${environment.apiUrl}/api/ai`;

  constructor(private http: HttpClient) {}

  query(question: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/query`, { question });
  }
}
