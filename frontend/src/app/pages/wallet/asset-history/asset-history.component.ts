import { Component } from '@angular/core';
import {TranslatePipe} from "@ngx-translate/core";
import {Asset} from '../asset-model';
import {HttpClient} from '@angular/common/http';
import {NgForOf} from '@angular/common';
import {WalletFooterNavbarComponent} from '../wallet-footer-navbar/wallet-footer-navbar.component';
import {Router} from '@angular/router';
import {environment} from '../../../../environments/environment.development';
import {API_ENDPOINTS} from '../../../services/api-endpoints';

@Component({
  selector: 'app-asset-history',
  standalone: true,
  imports: [
    TranslatePipe,
    NgForOf,
    WalletFooterNavbarComponent
  ],
  templateUrl: './asset-history.component.html',
  styleUrl: './asset-history.component.css'
})
export class AssetHistoryComponent {
  protected _assets: Asset[] = [];

  constructor(private http: HttpClient, private router: Router) {
    this.fetchWalletAssetsHistory();
  }

  fetchWalletAssetsHistory() {
    this.http.get<Asset[]>(`${environment.baseUrl}${API_ENDPOINTS.WALLET_HISTORY}`).subscribe({
      next: (assets) => {
        this._assets = Array.isArray(assets) ? assets : [];
      },
      error: (err) => {
        console.error(err);
        this._assets = [];
      },
    });
  }

  editAsset(asset: Asset) {
    this.router.navigate(['edit-asset'], { state: {asset} });
  }

  deleteAsset(assetId: number) {
    this.http.delete(`${environment.baseUrl}${API_ENDPOINTS.ASSET}/${assetId}`).subscribe({
      next: () => {
        this._assets = this._assets.filter(asset => asset.id !== assetId);
        console.log(`Asset with ID ${assetId} deleted successfully.`);
      },
      error: (err) => {
        console.error(err);
      }
    })
  }
}
