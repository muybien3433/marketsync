import { Routes } from '@angular/router';
import {AppComponent} from './app.component';
import {WalletComponent} from './wallet/wallet.component';

export const routes: Routes = [
  { path: '', component: AppComponent },
  { path: 'wallet', component: WalletComponent },
];
