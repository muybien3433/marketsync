import { Routes } from '@angular/router';
import {WalletComponent} from './pages/wallet/wallet.component';
import {SubscriptionComponent} from './pages/subscription/subscription.component';
import {AuthGuard} from './services/auth.guard';
import {HomeComponent} from './pages/home/home.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'wallet', component: WalletComponent, canActivate: [AuthGuard] },
  { path: 'subscription', component: SubscriptionComponent, canActivate: [AuthGuard] },
];
