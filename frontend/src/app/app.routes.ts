import { Routes } from '@angular/router';
import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';
import {WalletComponent} from './wallet/wallet.component';
import {AppComponent} from './app.component';
import {SubscriptionComponent} from './subscription/subscription.component';

export const routes: Routes = [
  { path: '', component: AppComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'wallet', component: WalletComponent },
  { path: 'subscription', component: SubscriptionComponent },
];
