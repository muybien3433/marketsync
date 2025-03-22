import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdminComponent } from './admin/admin.component';

const routes: Routes = [
  {
    path: '',
    component: AdminComponent,
    children: [
      {
        path: '',
        redirectTo: 'wallet',
        pathMatch: 'full'
      },
      {
        path: 'wallet',
        loadComponent: () => import('./pages/wallet/wallet.component').then((c) => c.WalletComponent)
      },
      {
        path: 'basic',
        loadChildren: () => import('./szablony/ui-basic/ui-basic.module').then((m) => m.UiBasicModule)
      },
      {
        path: 'apexchart',
        loadComponent: () => import('./pages/core-chart/apex-chart/apex-chart.component')
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
