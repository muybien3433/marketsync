import { Routes } from '@angular/router';
import {LoginComponent} from './pages/login/login.component';
import {RegisterComponent} from './pages/register/register.component';
import {WalletComponent} from './pages/wallet/wallet.component';
import {AppComponent} from './app.component';
import {SubscriptionComponent} from './pages/subscription/subscription.component';

export const routes: Routes = [
  { path: '', component: AppComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'wallet', component: WalletComponent },
  { path: 'subscription', component: SubscriptionComponent },
];
