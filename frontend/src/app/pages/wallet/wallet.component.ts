import { Component } from '@angular/core';
import {CurrencyPipe, NgForOf, NgStyle} from '@angular/common';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import {Asset} from "./asset-model";
import {
  WalletFooterNavbarComponent
} from "./wallet-footer-navbar/wallet-footer-navbar.component";
import {environment} from '../../../environments/environment.development';
import {API_ENDPOINTS} from '../../services/api-endpoints';
import {PreferenceService} from '../../services/preference-service';
import {CurrencyComponent} from '../currency/currency.component';

@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [NgForOf, NgStyle, TranslatePipe, WalletFooterNavbarComponent, CurrencyPipe, CurrencyComponent],
  templateUrl: './wallet.component.html',
  styleUrls: ['./wallet.component.css'],
})
export class WalletComponent {
  private _assets: Asset[] = [];
  protected readonly Object = Object;
  groupedAssets: { [key: string]: Asset[] } = {};

  constructor(private translate: TranslateService, private http: HttpClient, private preference: PreferenceService) {
    this.fetchWalletAssets();
  }

  fetchWalletAssets() {
    const preferredCurrency = this.preference.getPreferredCurrency();

    this.http.get<Asset[]>(`${environment.baseUrl}${API_ENDPOINTS.WALLET}/${preferredCurrency}`).subscribe({
      next: (assets) => {
        this._assets = Array.isArray(assets) ? assets : [];
        this.groupAssetsByType();
        console.log(preferredCurrency);
      },
      error: (err) => {
        console.error(err);
        this._assets = [];
      },
    });
  }

  groupAssetsByType() {
    this.groupedAssets = {};
    this._assets.forEach((asset) => {
      this.translate
          .get(`asset.type.${asset.assetType}`)
          .subscribe((translatedType) => {
            this.groupedAssets[translatedType] = this.groupedAssets[translatedType] || [];
            this.groupedAssets[translatedType].push(asset);
          });
    });
  }

  getTotalProfit(): number {
    return parseFloat(this._assets.reduce((sum, asset) => sum + asset.profit, 0).toFixed(2));
  }

  getTotalProfitInPercentage(): number {
    const totalValue = this._assets.reduce((sum, asset) => sum + (asset.value || 0), 0);
    const totalProfit = this._assets.reduce((sum, asset) => sum + ((asset.profitInPercentage || 0) * asset.value), 0);
    const profitInPercentage = totalValue > 0 ? (totalProfit / totalValue) : 0;

    return parseFloat(profitInPercentage.toFixed(2));
  }

  getTotalValue() {
    return this._assets.reduce((total, asset) => total +(asset.value || 0), 0);
  }
}
