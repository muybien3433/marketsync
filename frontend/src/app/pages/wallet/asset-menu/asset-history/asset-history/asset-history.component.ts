import { Component } from '@angular/core';
import {TranslatePipe} from "@ngx-translate/core";
import {Asset} from '../../../asset-model';
import {HttpClient} from '@angular/common/http';
import {NgForOf} from '@angular/common';
import {WalletFooterNavbarComponent} from '../../../wallet-footer-navbar/wallet-footer-navbar.component';

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
  private _baseUrl = 'http://localhost:9999/api/v1/wallets';
  protected _assets: Asset[] = [];

  constructor(private http: HttpClient) {
    this.fetchWalletAssetsHistory();
  }

  fetchWalletAssetsHistory() {
    this.http.get<Asset[]>(this._baseUrl + '/history').subscribe({
      next: (assets) => {
        this._assets = Array.isArray(assets) ? assets : [];
      },
      error: (err) => {
        console.error('Error fetching wallet assets', err);
        this._assets = [];
      },
    });
  }

  editAsset() {

  }

  deleteAsset(assetId: number) {
    this.http.delete(`${this._baseUrl}/assets/${assetId}`).subscribe({
      next: () => {
        this._assets = this._assets.filter(asset => asset.id !== assetId);
        console.log(`Asset with ID ${assetId} deleted successfully.`);
      },
      error: (err) => {
        console.error(`Error deleting asset with ID ${assetId}`, err);
      }
    })
  }
}
