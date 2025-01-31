import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {TranslatePipe} from '@ngx-translate/core';
import {WalletFooterNavbarComponent} from '../../navbar/wallet-footer-navbar/wallet-footer-navbar.component';
import {NgForOf, NgIf} from '@angular/common';
import {environment} from '../../../../environments/environment.development';
import {API_ENDPOINTS} from '../../../services/api-endpoints';
import {Currency} from "../../../models/currency";
import {AssetType} from "../../../models/asset-type";
import {AssetSelectionService} from "../../../services/asset-selection-service";
import {AssetSelectionListComponent} from "../asset-selection-list/asset-selection-list.component";
import {Router} from "@angular/router";

@Component({
    selector: 'app-add-asset',
    standalone: true,
    imports: [
        FormsModule,
        TranslatePipe,
        WalletFooterNavbarComponent,
        NgIf,
        ReactiveFormsModule,
        NgForOf,
        AssetSelectionListComponent
    ],
    templateUrl: './add-asset.component.html',
    styleUrls: ['./add-asset.component.css'],
})
export class AddAssetComponent implements OnInit {
    addAssetForm!: FormGroup;
    isSubmitting = false;
    successMessage: string = '';
    errorMessage: string = '';
    selectedAssetType: string = '';
    selectedAssetUri: string = '';
    assetTypesOptions = Object.values(AssetType);
    currencyOptions = Object.values(Currency);

    constructor(
        private fb: FormBuilder,
        private http: HttpClient,
        private router: Router,
        private assetSelection: AssetSelectionService,
    ) {
        this.addAssetForm = this.fb.group({
            assetType: ['', Validators.required],
            uri: ['', [Validators.required, Validators.minLength(1)]],
            count: ['0.0', [Validators.required, Validators.min(0.00000001)]],
            purchasePrice: ['0.0', [Validators.required, Validators.min(0.01)]],
            currency: ['', [Validators.required]],
        });
    }

    ngOnInit(): void {
        this.assetSelection.selectedAssetType$.subscribe(assetType => {
            this.selectedAssetType = assetType;
            this.addAssetForm.get('assetType')?.setValue(assetType);
        })
        this.assetSelection.selectedAssetUri$.subscribe(uri => {
            this.selectedAssetUri = uri;
            this.addAssetForm.get('uri')?.setValue(uri);
        })
    }

    onSubmit(): void {
        if (this.addAssetForm.invalid || !this.selectedAssetUri) {
            this.errorMessage = 'Please select an asset.';
            return;
        }
        this.isSubmitting = true;

        const asset = {
            assetType: this.selectedAssetType,
            uri: this.selectedAssetUri,
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

    addAsset(assetData: {
        assetType: string; uri: string; count: number;
        purchasePrice: number; currency: string
    }) {
        return this.http.post(`${environment.baseUrl}${API_ENDPOINTS.WALLET}`, assetData);
    }
}
