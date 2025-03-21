import { Component } from '@angular/core';
import {TranslatePipe} from "@ngx-translate/core";
import {HttpClient} from '@angular/common/http';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {WalletFooterNavbarComponent} from '../wallet-footer-navbar/wallet-footer-navbar.component';
import {Router} from '@angular/router';
import {environment} from '../../../../environments/environment';
import {API_ENDPOINTS} from '../../../services/api-endpoints';
import {AssetHistory} from '../../../models/asset-history';

@Component({
  selector: 'app-wallet-asset-history',
  standalone: true,
  imports: [
    TranslatePipe,
    NgForOf,
    WalletFooterNavbarComponent,
    DatePipe,
    NgIf,
  ],
  templateUrl: './wallet-asset-history.component.html',
  styleUrl: './wallet-asset-history.component.css'
})
export class WalletAssetHistoryComponent {
  protected _assets: AssetHistory[] = [];

  constructor(private http: HttpClient, private router: Router) {
    this.fetchWalletAssetsHistory();
  }

  fetchWalletAssetsHistory() {
    this.http.get<AssetHistory[]>(`${environment.baseUrl}${API_ENDPOINTS.WALLET_HISTORY}`).subscribe({
      next: (assets) => {
        this._assets = Array.isArray(assets) ? assets : [];
      },
      error: (err) => {
        console.error(err);
        this._assets = [];
      },
    });
  }

  editAsset(asset: AssetHistory) {
    this.router.navigate(['wallet-edit-asset'], { state: {asset} });
  }

  deleteAsset(assetId: number) {
    this.http.delete(`${environment.baseUrl}${API_ENDPOINTS.WALLET}/${assetId}`).subscribe({
      next: () => {
        this._assets = this._assets.filter(asset => asset.id !== assetId);
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  addAssetButton() {
    this.router.navigate(['wallet-add-asset']);
  }
}
