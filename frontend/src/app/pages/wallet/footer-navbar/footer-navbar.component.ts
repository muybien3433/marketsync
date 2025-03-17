import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-wallet-footer-navbar',
  standalone: true,
  imports: [
    TranslatePipe
  ],
  templateUrl: './footer-navbar.component.html',
  styleUrl: './footer-navbar.component.css'
})
export class FooterNavbarComponent {
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
