import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {TranslatePipe} from '@ngx-translate/core';
import {WalletFooterNavbarComponent} from '../wallet-footer-navbar/wallet-footer-navbar.component';
import {NgIf} from '@angular/common';
import {environment} from '../../../../environments/environment';
import {API_ENDPOINTS} from '../../../services/api-endpoints';
import {AssetService} from "../../../services/asset.service";
import {AssetSelectionListComponent} from "../../common/asset-selection-list/asset-selection-list.component";
import {Router} from "@angular/router";
import {AssetPriceDisplayComponent} from "../../common/asset-price-display/asset-price-display.component";
import {AssetDetail} from "../../../models/asset-detail";
import {CurrencyChangeOptionComponent} from "../../common/currency-change-option/currency-change-option.component";
import {CurrencyService} from "../../../services/currency-service";
import {Subscription} from "rxjs";
import {CurrencyType} from "../../../models/currency-type";
import {AssetType} from "../../../models/asset-type";

@Component({
    selector: 'app-wallet-add-asset',
    standalone: true,
    imports: [
        FormsModule,
        TranslatePipe,
        WalletFooterNavbarComponent,
        NgIf,
        ReactiveFormsModule,
        AssetSelectionListComponent,
        AssetPriceDisplayComponent,
        CurrencyChangeOptionComponent
    ],
    templateUrl: './wallet-add-asset.component.html',
    styleUrls: ['./wallet-add-asset.component.css'],
})
export class WalletAddAssetComponent implements OnInit, OnDestroy {
    addAssetForm: FormGroup;
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
            count: ['0.01', [Validators.required, Validators.min(0.00000001)]],
            purchasePrice: ['0.01', [Validators.required, Validators.min(0.0001)]],
            currency: [currencyService.getSelectedCurrencyType(), [Validators.required]]
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
        this.currencyTypeSubscription = this.currencyService.selectedCurrencyType$.subscribe(currencyType => {
            this.currencyType = currencyType;
            this.addAssetForm.get('currency')?.setValue(currencyType);
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
            currency: formValue.currency,
        };

        this.addAsset(asset)?.subscribe({
            next: () => {
                this.isSubmitting = false;
                this.router.navigate(['wallet']);
            },
            error: () => {
                this.errorMessage = 'Failed to add asset.';
                this.isSubmitting = false;
            }
        });
    }

    addAsset(asset: {
        assetType: string; uri: string; count: number;
        purchasePrice: number; currency: string
    }) {
        return this.http.post(`${environment.baseUrl}${API_ENDPOINTS.WALLET}`, asset);
    }
}
