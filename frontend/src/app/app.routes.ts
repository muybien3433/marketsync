import { Routes } from '@angular/router';
import { AdminComponent } from "./common/admin/admin.component";
import { AuthGuard } from "./common/service/auth-guard";

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'wallet/assets'
    },
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
                path: 'asset/edit',
                canActivate: [AuthGuard],
                loadComponent: () => import('./pages/wallet/wallet-edit-asset/wallet-edit-asset.component')
            },
            {
                path: 'assets/history',
                canActivate: [AuthGuard],
                loadComponent: () => import('./pages/wallet/wallet-asset-history/wallet-asset-history.component')
            },
        ]
    },
    {
        path: 'subscription',
        component: AdminComponent,
        children: [
            {
                path: 'subscriptions',
                canActivate: [AuthGuard],
                loadComponent: () => import('./pages/subscription/subscription.component')
            },
            {
                path: 'add',
                canActivate: [AuthGuard],
                loadComponent:() => import('./pages/subscription/subscription-add/subscription-add.component')
            }
        ]
    },
    {
        path: 'settings',
        canActivate: [AuthGuard],
        children: [
            {
                path: 'currency',
                canActivate: [AuthGuard],
                loadComponent:() => import('./pages/settings/settings.component')
            }
        ]
    },
    {
        path: '**',
        redirectTo: 'wallet/assets'
    }
];
