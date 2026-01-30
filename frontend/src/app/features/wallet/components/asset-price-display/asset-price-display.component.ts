import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";
import {TranslatePipe} from "@ngx-translate/core";
import {combineLatest, Subscription} from "rxjs";
import {AssetDetail} from "../../models/asset-detail.model";
import {CurrencyType} from "../../../../shared/enum/currency-type";
import {CommonModule} from '@angular/common';
import {WalletApi} from "../../data-access/wallet.api";
import {CurrencyService} from "../../../../core/services/currency.service";

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
    private readonly assetService = inject(WalletApi);
    private readonly currencyService = inject(CurrencyService);

    private combinedSubscription!: Subscription;
    currentCurrency!: CurrencyType;
    CurrencyType = CurrencyType;
    currentAsset: AssetDetail | null = null;

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
