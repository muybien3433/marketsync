import { Routes } from '@angular/router';
import { AdminComponent } from "./common/admin/admin.component";

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
                loadComponent: () => import('./pages/wallet/wallet.component')
            },
            {
                path: 'asset/add',
                loadComponent: () => import('./pages/wallet/wallet-add-asset/wallet-add-asset.component')
            },
            {
                path: 'asset/edit',
                loadComponent: () => import('./pages/wallet/wallet-edit-asset/wallet-edit-asset.component')
            },
            {
                path: 'assets/history',
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
                loadComponent: () => import('./pages/subscription/subscription.component')
            },
            {
                path: 'add',
                loadComponent:() => import('./pages/subscription/subscription-add/subscription-add.component')
            }
        ]
    },
    {
        path: 'settings',
        component: AdminComponent,
        children: [
            {
                path: 'currency',
                loadComponent:() => import('./pages/settings/settings.component')
            }
        ]
    },
    {
        path: '**',
        redirectTo: 'wallet/assets'
    }
];
