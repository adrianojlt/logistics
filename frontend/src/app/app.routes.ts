import { Routes } from '@angular/router';
import { CalculateProfitComponent } from './pages/calculate-profit/calculate-profit.component';

export const routes: Routes = [
  { path: 'calculate-profit', component: CalculateProfitComponent },
  { path: '', redirectTo: 'calculate-profit', pathMatch: 'full' },
];
