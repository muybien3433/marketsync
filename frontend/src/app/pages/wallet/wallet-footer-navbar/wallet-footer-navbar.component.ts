import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {TranslatePipe} from '@ngx-translate/core';
import {CurrencyComponent} from '../../currency/currency.component';

@Component({
  selector: 'app-wallet-footer-navbar',
  standalone: true,
  imports: [
    TranslatePipe,
    CurrencyComponent
  ],
  templateUrl: './wallet-footer-navbar.component.html',
  styleUrl: './wallet-footer-navbar.component.css'
})
export class WalletFooterNavbarComponent {
  constructor(private router: Router) {
  }

  backToWallet() {
    this.router.navigate(['/wallet']);
  }

  addAsset() {
    this.router.navigate(['/add-asset']);
  }

  assetHistory() {
    this.router.navigate(['/asset-history']);
  }
}
