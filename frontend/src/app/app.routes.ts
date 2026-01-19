import { Routes } from '@angular/router';
import { BeneficioListComponent } from './beneficio/beneficio-list/beneficio-list.component';
import { BeneficioFormComponent } from './beneficio/beneficio-form/beneficio-form.component';
import { TransferenciaComponent } from './transferencia/transferencia.component';

export const routes: Routes = [
  { path: '', redirectTo: '/beneficios', pathMatch: 'full' },
  { path: 'beneficios', component: BeneficioListComponent },
  { path: 'beneficios/novo', component: BeneficioFormComponent },
  { path: 'beneficios/editar/:id', component: BeneficioFormComponent },
  { path: 'transferencia', component: TransferenciaComponent }
];
