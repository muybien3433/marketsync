import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";
import {TranslatePipe} from "@ngx-translate/core";
import {combineLatest, Subscription} from "rxjs";
import {AssetDetail} from "../../model/asset-detail.model";
import {CurrencyType} from "../../enum/currency-type";
import {AssetService} from "../../service/asset-service";
import {CurrencyService} from "../../service/currency-service";
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-asset-price-display',
  standalone: true,
  imports: [
    TranslatePipe,
    NgIf,
    CommonModule
  ],
  templateUrl: './asset-price-display.component.html',
  styleUrl: './asset-price-display.component.css'
})
export class AssetPriceDisplayComponent implements OnInit, OnDestroy {

  private combinedSubscription!: Subscription;
  currentCurrency!: CurrencyType;
  CurrencyType = CurrencyType;
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
