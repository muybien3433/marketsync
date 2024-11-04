import { Routes } from '@angular/router';
import {AppComponent} from './app.component';
import {WalletComponent} from './wallet/wallet.component';
import {LoginComponent} from './login/login.component';

export const routes: Routes = [
  { path: '', component: AppComponent },
  { path: 'wallet', component: WalletComponent },
  { path: 'login', component: LoginComponent },
];
