import { Routes } from '@angular/router';
import {WalletComponent} from './pages/wallet/wallet.component';
import {SubscriptionComponent} from './pages/subscription/subscription.component';
import {AuthGuard} from './services/auth.guard';
import {HomeComponent} from './pages/home/home.component';
import {AddAssetComponent} from './pages/wallet/asset-menu/add-asset/add-asset.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'wallet', component: WalletComponent, canActivate: [AuthGuard] },
  { path: 'add-asset', component: AddAssetComponent, canActivate: [AuthGuard] },
  { path: 'subscription', component: SubscriptionComponent, canActivate: [AuthGuard] },
];
