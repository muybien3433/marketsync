import { Routes } from '@angular/router';
import { AdminComponent } from './common/admin/admin.component';
import { authGuard } from './common/service/auth-guard';
import WalletComponent from './pages/wallet/wallet.component';
import WalletAddAssetComponent from './pages/wallet/wallet-add-asset/wallet-add-asset.component';
import WalletEditAssetComponent from './pages/wallet/wallet-edit-asset/wallet-edit-asset.component';
import WalletAssetHistoryComponent from './pages/wallet/wallet-asset-history/wallet-asset-history.component';
import SubscriptionComponent from './pages/subscription/subscription.component';
import SubscriptionAddComponent from './pages/subscription/subscription-add/subscription-add.component';
import SettingsComponent from './pages/settings/settings.component';
import {LoginComponent} from "./pages/auth/login/login.component";
import {RegisterComponent} from "./pages/auth/register/register.component";

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'wallet/assets',
    },
    {
        path: 'auth',
        children: [
            {
                path: 'login',
                component: LoginComponent,
            },
            {
                path: 'register',
                component: RegisterComponent
            }
        ],
    },
    {
        path: 'wallet',
        canActivate: [authGuard],
        component: AdminComponent,
        children: [
            {
                path: 'assets',
                component: WalletComponent,
            },
            {
                path: 'asset/add',
                component: WalletAddAssetComponent,
            },
            {
                path: 'asset/edit',
                component: WalletEditAssetComponent,
            },
            {
                path: 'assets/history',
                component: WalletAssetHistoryComponent,
            },
        ],
    },
    {
        path: 'subscription',
        canActivate: [authGuard],
        component: AdminComponent,
        children: [
            {
                path: 'subscriptions',
                component: SubscriptionComponent,
            },
            {
                path: 'add',
                component: SubscriptionAddComponent,
            },
        ],
    },
    {
        path: 'settings',
        canActivate: [authGuard],
        component: AdminComponent,
        children: [
            {
                path: 'currency',
                component: SettingsComponent,
            },
        ],
    },
    {
        path: '**',
        redirectTo: 'wallet/assets',
    },
];
