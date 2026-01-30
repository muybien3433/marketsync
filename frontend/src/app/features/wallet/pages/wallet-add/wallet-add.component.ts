import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {NgIf} from '@angular/common';
import {Router} from "@angular/router";
import {Subscription} from "rxjs";
import {
    AssetSelectionListComponent
} from "../../components/asset-selection-list/asset-selection-list.component";
import {AssetPriceDisplayComponent} from "../../components/asset-price-display/asset-price-display.component";
import {
    CurrencyChangeOptionComponent
} from "../../../../shared/components/currency-change-option/currency-change-option.component";
import {CurrencyType} from "../../../../shared/enum/currency-type";
import {AssetType} from "../../../../shared/enum/asset-type";
import {AssetDetail} from "../../models/asset-detail.model";
import {API_ENDPOINTS} from "../../../../core/http/api/api-endpoints";
import {CardComponent} from "../../../../shared/ui/card/card.component";
import {NumberInputDirective} from "../../../../shared/form/number-input/number-input.directive";
import {UnitType, UnitTypeLabels} from "../../../../shared/enum/unit-type";
import {WalletApi} from "../../data-access/wallet.api";
import {CurrencyService} from "../../../../core/services/currency.service";

@Component({
    selector: 'app-wallet-add',
    standalone: true,
    imports: [
        FormsModule,
        TranslatePipe,
        NgIf,
        ReactiveFormsModule,
        AssetSelectionListComponent,
        AssetPriceDisplayComponent,
        CurrencyChangeOptionComponent,
        CardComponent,
        NumberInputDirective
    ],
    templateUrl: './wallet-add.component.html',
    styleUrls: ['./wallet-add.component.scss'],
})
export default class WalletAddComponent implements OnInit, OnDestroy {
    private readonly fb = inject(FormBuilder);
    private readonly http = inject(HttpClient);
    private readonly router = inject(Router);
    private readonly assetService = inject(WalletApi);
    private readonly currencyService = inject(CurrencyService);
    private readonly translate = inject(TranslateService);

    public addAssetForm: FormGroup;
    isSubmitting: boolean = false;
    errorMessage: string = '';
    maxCommentLength: number = 200;
    remainingCommentChars: number = this.maxCommentLength;
    maxUnitLength: number = 6;
    remainingUnitChars: number = this.maxUnitLength;

    private currencyTypeSubscription!: Subscription;
    currencyType!: CurrencyType;

    private assetTypeSubscription!: Subscription;
    assetType!: AssetType;

    protected assetSubscription!: Subscription;
    asset: AssetDetail | null = null;

    constructor() {
        this.addAssetForm = this.fb.group({
            assetType: ['', Validators.required],
            unitType: [UnitType.UNIT, Validators.maxLength(6)],
            uri: ['', [Validators.required, Validators.minLength(1)]],
            count: ['0.01', [Validators.required, Validators.min(0.0000000000001)]],
            purchasePrice: [ '0.01', [Validators.required, Validators.min(0.0000000000001)]],
            currentPrice: ['0.01', [Validators.min(0.0000000000001)]],
            currencyType: [this.currencyService.getGlobalCurrencyType(), [Validators.required]],
            comment: ['', [Validators.maxLength(this.maxCommentLength)]],
        });
    }

    ngOnInit(): void {
        this.assetTypeSubscription = this.assetService.selectedAssetType$.subscribe(assetType => {
            this.assetType = assetType;
            this.addAssetForm.get('assetType')?.setValue(assetType);
        })
        this.assetSubscription = this.assetService.selectedAsset$.subscribe(asset => {
            this.asset = asset;
            this.addAssetForm.get('uri')?.setValue(asset?.uri);
        })
        this.currencyTypeSubscription = this.currencyService.selectedCurrencyType$
            .subscribe(currencyType => {
                this.currencyType = currencyType;
                this.addAssetForm.get('currencyType')?.setValue(currencyType);
            })
        this.addAssetForm.get('comment')?.valueChanges.subscribe(value => {
            const length = value?.length || 0;
            this.remainingCommentChars = this.maxCommentLength - length;

            if (this.remainingCommentChars < 0) {
                const truncatedValue = value.substring(0, this.maxCommentLength + Math.abs(0));
                this.addAssetForm.get('comment')?.setValue(truncatedValue, { emitEvent: false });
                this.remainingCommentChars = 0;
            }
        });
        this.remainingUnitChars = this.maxUnitLength - (this.asset?.unitType?.length || 0);
        this.addAssetForm.get('unitType')?.valueChanges.subscribe(value => {
            const length = value?.length || 0;
            this.remainingUnitChars = this.maxUnitLength - length;

            if (this.remainingUnitChars < 0) {
                const truncatedValue = value.substring(0, this.maxUnitLength);
                this.addAssetForm.get('unitType')?.setValue(truncatedValue, {emitEvent: false});
                this.remainingUnitChars = 0;
            }
        });
    }

    ngOnDestroy(): void {
        this.currencyTypeSubscription.unsubscribe();
        this.assetTypeSubscription.unsubscribe();
        this.assetSubscription.unsubscribe();
    }

    onAssetChange(asset: AssetDetail) {
        this.asset = asset;
        this.addAssetForm.get('uri')?.setValue(asset ? asset.uri : '');
        if (asset) {
            this.addAssetForm.get('assetType')?.setValue(asset.assetType);
        }
    }

    currencyIsSame(): boolean {
        return this.currencyType === this.asset?.currencyType;
    }

    onSubmit(): void {
        if (this.addAssetForm.invalid) {
            this.errorMessage = this.translate.instant('error.asset.is.required');
            return;
        }
        this.isSubmitting = true;

        const formValue = this.addAssetForm.value;
        const asset = {
            assetType: formValue.assetType,
            unitType: formValue.unitType,
            uri: formValue.uri,
            count: formValue.count,
            purchasePrice: this.resolvePurchasePrice(formValue.purchasePrice),
            currentPrice: formValue.currentPrice,
            currencyType: formValue.currencyType,
            comment: formValue.comment,
        };

        this.addAsset(asset)?.subscribe({
            next: () => {
                this.isSubmitting = false;
                this.router.navigate(['wallet/assets']);
            },
            error: () => {
                this.errorMessage = this.translate.instant('error.asset.add.fail');
                this.isSubmitting = false;
            }
        });
    }

    resolvePurchasePrice(purchasePrice: number) {
        if (this.assetType === AssetType.CURRENCY && this.currencyIsSame()) {
            return 1;
        }
        return purchasePrice;
    }

    addAsset(asset: {
        assetType: string;
        uri: string;
        count: number;
        unitType: string;
        purchasePrice: number;
        currentPrice: number;
        currencyType: string;
        comment: string;
    }) {
        console.log(asset)
        return this.http.post(`${API_ENDPOINTS.WALLET}`, asset);
    }

    protected readonly UnitTypeLabels = UnitTypeLabels;
}
