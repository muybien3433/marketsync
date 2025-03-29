import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {TranslatePipe} from '@ngx-translate/core';
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
    isSubmitting = false;
    errorMessage: string = '';

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
    ) {
        this.addAssetForm = this.fb.group({
            assetType: ['', Validators.required],
            uri: ['', [Validators.required, Validators.minLength(1)]],
            count: ['0.01', [Validators.required, Validators.min(0.0000000000001)]],
            purchasePrice: ['0.01', [Validators.required, Validators.min(0.0000000000001)]],
            currencyType: [currencyService.getGlobalCurrencyType(), [Validators.required]]
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

    onSubmit(): void {
        if (this.addAssetForm.invalid) {
            this.errorMessage = 'Please select an asset.';
            return;
        }
        this.isSubmitting = true;

        const formValue = this.addAssetForm.value;
        const asset = {
            assetType: formValue.assetType,
            uri: formValue.uri,
            count: formValue.count,
            purchasePrice: formValue.purchasePrice,
            currencyType: formValue.currencyType,
        };

        this.addAsset(asset)?.subscribe({
            next: () => {
                this.isSubmitting = false;
                this.router.navigate(['wallet/assets']);
            },
            error: () => {
                this.errorMessage = 'Failed to add asset.';
                this.isSubmitting = false;
            }
        });
    }

    addAsset(asset: {
        assetType: string; uri: string; count: number;
        purchasePrice: number; currencyType: string
    }) {
        return this.http.post(`${environment.baseUrl}${API_ENDPOINTS.WALLET}`, asset);
    }
}
