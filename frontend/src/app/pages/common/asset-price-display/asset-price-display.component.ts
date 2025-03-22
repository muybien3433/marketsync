import {Component, OnDestroy, OnInit} from '@angular/core';
import {AssetDetail} from "../../../models/asset-detail";
import {CurrencyPipe, NgIf} from "@angular/common";
import {TranslatePipe} from "@ngx-translate/core";
import {combineLatest, Subscription} from "rxjs";
import {AssetService} from "../../../services/asset.service";
import {CurrencyType} from "../../../models/currency-type";
import {CurrencyService} from "../../../services/currency-service";

@Component({
  selector: 'app-asset-price-display',
  standalone: true,
  imports: [
    CurrencyPipe,
    TranslatePipe,
    NgIf
  ],
  templateUrl: './asset-price-display.component.html',
  styleUrl: './asset-price-display.component.css'
})
export class AssetPriceDisplayComponent implements OnInit, OnDestroy {

  private combinedSubscription!: Subscription;
  currentCurrency!: CurrencyType;
  currentAsset: AssetDetail | null = null;

  constructor(private assetService: AssetService, private currencyService: CurrencyService) {}

  ngOnInit(): void {
    this.combinedSubscription = combineLatest([
      this.currencyService.selectedCurrencyType$,
      this.assetService.selectedAsset$
    ]).subscribe(([currency, asset]) => {
      this.currentCurrency = currency;
      this.currentAsset = asset;
    });
  }

  ngOnDestroy(): void {
    this.combinedSubscription.unsubscribe();
  }
}
