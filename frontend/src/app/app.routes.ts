import {Routes} from "@angular/router";
import {authGuard} from "./core/guards/auth-guard";

export const routes: Routes = [
    {
        path: 'auth',
        loadChildren: () =>
            import('./features/auth/routes').then(m => m.AUTH_ROUTES),
    },
    {
        path: '',
        canActivate: [authGuard],
        loadComponent: () => import('./core/layout/admin/shell.component').then(m => m.ShellComponent),
        children: [
            {
                path: 'setting',
                loadChildren: () => import('./features/setting/routes').then(m => m.SETTING_ROUTES),
            },
            {
                path: 'subscription',
                loadChildren: () => import('./features/subscription/routes').then(m => m.SUBSCRIPTION_ROUTES),
            },
            {
                path: 'wallet',
                loadChildren: () => import('./features/wallet/routes').then(m => m.WALLET_ROUTES),
            }
        ],
    },

    { path: '**', redirectTo: '' },
];