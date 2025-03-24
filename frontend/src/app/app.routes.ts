import { Routes } from '@angular/router';
import { AdminComponent } from "./admin/admin.component";
import { AuthGuard } from "./common/service/auth.guard";

export const routes: Routes = [
    {
        path: 'wallet',
        component: AdminComponent,
        children: [
            {
                path: 'assets',
                canActivate: [AuthGuard],
                loadComponent: () => import('./pages/wallet/wallet.component')
            },
            {
                path: 'asset/add',
                canActivate: [AuthGuard],
                loadComponent: () => import('./pages/wallet/wallet-add-asset/wallet-add-asset.component')
            },
            {
                path: 'assets/history',
                canActivate: [AuthGuard],
                loadComponent: () => import('./pages/wallet/wallet-asset-history/wallet-asset-history.component')
            },
            {
                path: 'apexchart',
                loadComponent: () => import('./pages/core-chart/apex-chart/apex-chart.component')
            }
        ]
    },
];
