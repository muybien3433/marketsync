import { Component } from '@angular/core';
import { Asset } from './asset';
import {NgForOf, NgStyle} from '@angular/common';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

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

  private _assets: Asset[] = [
    new Asset("CRYPTO", "Bitcoin", 200000, 100000, 100000, 1, 0.0, 0),
    new Asset("CRYPTO","Bitcoin", 100000, 50000, 100000, 1, 150.0, 50000),
    new Asset("STOCK", "Apple", 200000, 100000, 100000, 1, -10.0, -10000),
    new Asset("STOCK", "Tesla", 200000, 100000, 100000, 1, 0.0, 0),
  ];

  groupedAssets: { [key: string]: Asset[] } = {};

  constructor(private translate: TranslateService) {
    this.groupAssetsByType();
    this.translate.addLangs(['pl', 'en']);
    this.translate.setDefaultLang('en');
    this.translate.use('pl');
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
