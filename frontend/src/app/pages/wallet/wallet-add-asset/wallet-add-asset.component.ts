import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {NgIf} from '@angular/common';
import {environment} from '../../../../environments/environment';
import {Router} from "@angular/router";
import {Subscription} from "rxjs";
import {
    AssetSelectionListComponent
} from "../../../common/components/asset-selection-list/asset-selection-list.component";
import {AssetPriceDisplayComponent} from "../../../common/components/asset-price-display/asset-price-display.component";
import {
    CurrencyChangeOptionComponent
} from "../../../common/components/currency-change-option/currency-change-option.component";
import {CurrencyType} from "../../../common/model/currency-type";
import {AssetType} from "../../../common/model/asset-type";
import {AssetDetail} from "../../../common/model/asset-detail";
import {AssetService} from "../../../common/service/asset-service";
import {CurrencyService} from "../../../common/service/currency-service";
import {API_ENDPOINTS} from "../../../common/service/api-endpoints";
import {CardComponent} from "../../../common/components/card/card.component";
import {NumberInputDirective} from "../../../common/service/number-input.directive";
import {UnitType, UnitTypeLabels} from "../../../common/model/unit-type";

@Component({
    selector: 'app-wallet-add-asset',
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
    templateUrl: './wallet-add-asset.component.html',
    styleUrls: ['./wallet-add-asset.component.scss'],
})
export default class WalletAddAssetComponent implements OnInit, OnDestroy {
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

    constructor(
        private fb: FormBuilder,
        private http: HttpClient,
        private router: Router,
        private assetService: AssetService,
        private currencyService: CurrencyService,
        private translate: TranslateService,
    ) {
        this.addAssetForm = this.fb.group({
            assetType: ['', Validators.required],
            unitType: [UnitType.UNIT, Validators.maxLength(6)],
            uri: ['', [Validators.required, Validators.minLength(1)]],
            count: ['0.01', [Validators.required, Validators.min(0.0000000000001)]],
            purchasePrice: [ '0.01', [Validators.required, Validators.min(0.0000000000001)]],
            currentPrice: ['0.01', [Validators.min(0.0000000000001)]],
            currencyType: [currencyService.getGlobalCurrencyType(), [Validators.required]],
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
        return this.http.post(`${environment.baseUrl}${API_ENDPOINTS.WALLET}`, asset);
    }

    protected readonly UnitTypeLabels = UnitTypeLabels;
}
