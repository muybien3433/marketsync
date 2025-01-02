import { Component } from '@angular/core';
import {TranslatePipe} from "@ngx-translate/core";
import {HttpClient} from '@angular/common/http';
import {NgForOf} from '@angular/common';
import {WalletFooterNavbarComponent} from '../wallet-footer-navbar/wallet-footer-navbar.component';
import {Router} from '@angular/router';
import {environment} from '../../../../environments/environment.development';
import {API_ENDPOINTS} from '../../../services/api-endpoints';
import {AssetHistoryModel} from './asset-history-model';

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
  protected _assets: AssetHistoryModel[] = [];

  constructor(private http: HttpClient, private router: Router) {
    this.fetchWalletAssetsHistory();
  }

  fetchWalletAssetsHistory() {
    this.http.get<AssetHistoryModel[]>(`${environment.baseUrl}${API_ENDPOINTS.WALLET_HISTORY}`).subscribe({
      next: (assets) => {
        this._assets = Array.isArray(assets) ? assets : [];
      },
      error: (err) => {
        console.error(err);
        this._assets = [];
      },
    });
  }

  editAsset(asset: AssetHistoryModel) {
    this.router.navigate(['edit-asset'], { state: {asset} });
  }

  deleteAsset(assetId: number) {
    this.http.delete(`${environment.baseUrl}${API_ENDPOINTS.WALLET_HISTORY}/${assetId}`).subscribe({
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
