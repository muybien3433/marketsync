import {ChangeDetectorRef, Component, OnChanges, SimpleChanges} from '@angular/core';
import {CurrencyPipe, NgForOf, NgIf, NgStyle, SlicePipe, UpperCasePipe} from '@angular/common';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import {AssetAggregate} from "../../models/asset-aggregate";
import {
  WalletFooterNavbarComponent
} from "../navbar/wallet-footer-navbar/wallet-footer-navbar.component";
import {environment} from '../../../environments/environment.development';
import {API_ENDPOINTS} from '../../services/api-endpoints';
import {PreferenceService} from "../../services/preference-service";
import {CurrencyComponent} from "../currency/currency.component";

@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [NgForOf, NgStyle, TranslatePipe, WalletFooterNavbarComponent, CurrencyPipe, CurrencyComponent, NgIf, SlicePipe, UpperCasePipe],
  templateUrl: './wallet.component.html',
  styleUrls: ['./wallet.component.css'],
})
export class WalletComponent {
  private _assets: AssetAggregate[] = [];
  protected readonly Object = Object;
  groupedAssets: { [key: string]: AssetAggregate[] } = {};
  selectedCurrency: string;
  isLoading: boolean = false;

  constructor(
      private translate: TranslateService,
      private http: HttpClient,
      private preferenceService: PreferenceService
  ) {
    this.selectedCurrency = this.preferenceService.getPreferredCurrency() || 'USD';
    this.fetchWalletAssets();
  }

  fetchWalletAssets() {
    this.isLoading = true;
    this.http.get<AssetAggregate[]>(`${environment.baseUrl}${API_ENDPOINTS.WALLET}/${this.selectedCurrency}`).subscribe({
      next: (assets) => {
        this._assets = Array.isArray(assets) ? assets : [];
        this.groupAssetsByType();
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this._assets = [];
        this.isLoading = false;
      },
    });
  }

  groupAssetsByType() {
    this.groupedAssets = {};
    this._assets.forEach((asset) => {
      this.translate
          .get(`asset.assetType.${asset.assetType}`)
          .subscribe((translatedType) => {
            this.groupedAssets[translatedType] = this.groupedAssets[translatedType] || [];
            this.groupedAssets[translatedType].push(asset);
          });
    });
  }

  getTotalValue(): number {
    return this._assets.reduce((total, asset) => {
      const valueInSelectedCurrency = asset.value * asset.exchangeRateToDesired;
      return total + valueInSelectedCurrency;
    }, 0);
  }

  getTotalProfit(): number {
    return this._assets.reduce((total, asset) => {
      const profitInSelectedCurrency = asset.profit * asset.exchangeRateToDesired;
      return total + profitInSelectedCurrency;
    }, 0);
  }

  getTotalProfitInPercentage(): number {
    const totalValue = this.getTotalValue();
    const totalProfit = this.getTotalProfit();

    if (totalValue === 0) return 0;

    const profitInPercentage = (totalProfit / totalValue) * 100;
    return parseFloat(profitInPercentage.toFixed(2));
  }

  getCurrencyForSum() {
    return this.selectedCurrency;
  }

  onCurrencyChange(newCurrency: string) {
    this.preferenceService.setPreferredCurrency(newCurrency);
    this.selectedCurrency = newCurrency;
    this.fetchWalletAssets();
  }
}
