import {Component} from '@angular/core';
import {CurrencyPipe, NgForOf, NgIf, NgStyle} from '@angular/common';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import {AssetAggregate} from "../../models/asset-aggregate";
import {
  FooterNavbarComponent
} from "./footer-navbar/footer-navbar.component";
import {environment} from '../../../environments/environment';
import {API_ENDPOINTS} from '../../services/api-endpoints';
import {PreferenceService} from "../../services/preference-service";
import {CurrencyComponent} from "../currency/currency.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [NgForOf, NgStyle, TranslatePipe, FooterNavbarComponent, CurrencyPipe, CurrencyComponent, NgIf],
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
      private preferenceService: PreferenceService,
      private router: Router,
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
    const totalInvestment = this.getTotalInvestment();
    const totalProfit = this.getTotalProfit();

    if (totalInvestment === 0) return 0;

    const profitInPercentage = (totalProfit / totalInvestment) * 100;
    return parseFloat(profitInPercentage.toFixed(2));
  }

  getTotalInvestment(): number {
    return this._assets.reduce((total, asset) => {
      return total + (asset.averagePurchasePrice * asset.count * asset.exchangeRateToDesired);
    }, 0);
  }

  getCurrencyForSum() {
    return this.selectedCurrency;
  }

  onCurrencyChange(newCurrency: string) {
    this.preferenceService.setPreferredCurrency(newCurrency);
    this.selectedCurrency = newCurrency;
    this.fetchWalletAssets();
  }

  addAssetButton() {
    this.router.navigate(['add-asset']);
  }
}
