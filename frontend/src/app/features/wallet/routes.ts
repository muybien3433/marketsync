import {Routes} from "@angular/router";

export const WALLET_ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: 'assets',
                loadComponent:() => import('./pages/wallet-list/wallet-list.component').then(m => m.default),
            },
            {
                path: 'asset/add',
                loadComponent:() => import('./pages/wallet-add/wallet-add.component').then(m => m.default),
            },
            {
                path: 'asset/edit',
                loadComponent:() => import('./pages/wallet-edit/wallet-edit.component').then(m => m.default),
            },
            {
                path: 'assets/history',
                loadComponent:() => import('./pages/wallet-history/wallet-history.component').then(m => m.default),
            },
        ],
    },
];
