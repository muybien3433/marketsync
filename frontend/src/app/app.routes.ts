import { Routes } from '@angular/router';
import { AdminComponent } from "./admin/admin.component";
import { AuthGuard } from "./common/service/auth.guard";
import { WalletComponent } from "./pages/wallet/wallet.component";

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        component: AdminComponent,
        children: [
            {
                path: '',
                canActivate: [AuthGuard],
                component: WalletComponent
                // loadComponent: () => import('./pages/wallet/wallet.component').then((c) => c.WalletComponent)
            },
            {
                path: 'apexchart',
                loadComponent: () => import('./pages/core-chart/apex-chart/apex-chart.component')
            }
        ]
    },
];
