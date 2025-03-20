import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {TranslatePipe} from '@ngx-translate/core';
import {FooterNavbarComponent} from '../footer-navbar/footer-navbar.component';
import {NgForOf, NgIf} from '@angular/common';
import {environment} from '../../../../environments/environment';
import {API_ENDPOINTS} from '../../../services/api-endpoints';
import {CurrencyType} from "../../../models/currency-type";
import {AssetService} from "../../../services/asset.service";
import {AssetSelectionListComponent} from "../../asset-selection-list/asset-selection-list.component";
import {Router} from "@angular/router";
import {PreferenceService} from "../../../services/preference-service";
import {AssetPriceDisplayComponent} from "../../asset-price-display/asset-price-display.component";
import {AssetDetail} from "../../../models/asset-detail";

@Component({
    selector: 'app-add-asset',
    standalone: true,
    imports: [
        FormsModule,
        TranslatePipe,
        FooterNavbarComponent,
        NgIf,
        ReactiveFormsModule,
        NgForOf,
        AssetSelectionListComponent,
        AssetPriceDisplayComponent
    ],
    templateUrl: './add-asset.component.html',
    styleUrls: ['./add-asset.component.css'],
})
export class AddAssetComponent implements OnInit {
    addAssetForm: FormGroup;
    isSubmitting = false;
    selectedAssetType: string = '';
    selectedAsset: AssetDetail | undefined;
    uri: string = '';
    currencyOptions = Object.values(CurrencyType);
    errorMessage: string = '';
    assets: AssetDetail[] = [];

    constructor(
        private fb: FormBuilder,
        private http: HttpClient,
        private router: Router,
        private assetService: AssetService,
        private preferenceService: PreferenceService
    ) {
        this.addAssetForm = this.fb.group({
            assetType: ['', Validators.required],
            uri: ['', [Validators.required, Validators.minLength(1)]],
            count: ['0.01', [Validators.required, Validators.min(0.00000001)]],
            purchasePrice: ['0.01', [Validators.required, Validators.min(0.0001)]],
            currency: [this.preferenceService.getPreferredCurrency(), [Validators.required]],
        });
    }

    ngOnInit(): void {
        this.assetService.selectedAssetType$.subscribe(assetType => {
            this.selectedAssetType = assetType;
            this.addAssetForm.get('assetType')?.setValue(assetType);
        })
        this.assetService.selectedAssetUri$.subscribe(uri => {
            this.uri = uri;
            this.addAssetForm.get('uri')?.setValue(uri);
        })
    }

    onAssetSelected(asset: any) {
        if (asset) {
            this.selectedAsset = asset;
        } else {
            if (this.selectedAsset !== undefined) {
                this.errorMessage = 'Please select a valid asset.';
            }
        }
    }

    onAssetReset() {
        this.selectedAsset = undefined;
        this.addAssetForm.get('uri')?.setValue('');
    }

    onSubmit(): void {
        if (this.addAssetForm.invalid) {
            this.errorMessage = 'Please select an asset.';
            return;
        }
        this.isSubmitting = true;

        const asset = {
            assetType: this.selectedAssetType,
            uri: this.uri,
            count: this.addAssetForm.get('count')?.value,
            purchasePrice: this.addAssetForm.get('purchasePrice')?.value,
            currency: this.addAssetForm.get('currency')?.value,
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
