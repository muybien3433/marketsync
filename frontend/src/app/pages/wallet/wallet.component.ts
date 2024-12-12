import { Component } from '@angular/core';
import { Asset } from './asset';
import {NgForOf, NgStyle} from '@angular/common';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {KeycloakService} from 'keycloak-angular';

@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [
    NgForOf,
    NgStyle,
    TranslatePipe
  ],
  templateUrl: './wallet.component.html',
  styleUrl: './wallet.component.css'
})
export class WalletComponent {

  private _assets: Asset[] = [];
  groupedAssets: { [key: string]: Asset[] } = {};

  constructor(private translate: TranslateService, private http: HttpClient) {
    this.translate.addLangs(['pl', 'en']);
    this.translate.setDefaultLang('en');
    this.translate.use('pl');
    this.fetchWalletAssets();
  }

  fetchWalletAssets() {
    this.http.get<Asset[]>('http://localhost:9999/api/v1/wallets').subscribe({
      next: assets => {
        this._assets = Array.isArray(assets) ? assets : [];
        this.groupAssetsByType();
      },
      error: err => {
        console.error("Error fetching wallet assets", err);
        this._assets = [];
      }
    });
  }

  groupAssetsByType() {
    this.groupedAssets = {};
    this._assets.forEach(asset => {
      this.translate.get(`wallet.asset.type.${asset.type.toLowerCase()}`).subscribe(translatedType => {
        this.groupedAssets[translatedType] = this.groupedAssets[translatedType] || [];
        this.groupedAssets[translatedType].push(asset);
      });
    });
  }

  getTotalProfit(): number {
    return this._assets.reduce((sum, asset) => sum + asset.profit, 0);
  }

  getTotalProfitInPercentage(): number {
    return this._assets.reduce((sum, asset) => sum + asset.profitInPercentage, 0)
  }

  protected readonly Object = Object;
}
