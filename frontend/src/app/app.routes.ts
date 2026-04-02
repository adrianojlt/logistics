import { Routes } from '@angular/router';
import { CalculateProfitComponent } from './pages/calculate-profit/calculate-profit.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { LoginComponent } from './pages/login/login.component';
import { AskAiComponent } from './pages/ask-ai/ask-ai.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'calculate-profit', component: CalculateProfitComponent, canActivate: [authGuard] },
  { path: 'ask-ai', component: AskAiComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
];
