import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {TranslatePipe} from '@ngx-translate/core';
import {FooterNavbarComponent} from '../footer-navbar/footer-navbar.component';
import {NgForOf, NgIf} from '@angular/common';
import {environment} from '../../../../environments/environment';
import {API_ENDPOINTS} from '../../../services/api-endpoints';
import {Currency} from "../../../models/currency";
import {AssetService} from "../../../services/asset.service";
import {AssetSelectionListComponent} from "../../asset-selection-list/asset-selection-list.component";
import {Router} from "@angular/router";
import {PreferenceService} from "../../../services/preference-service";

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
        AssetSelectionListComponent
    ],
    templateUrl: './add-asset.component.html',
    styleUrls: ['./add-asset.component.css'],
})
export class AddAssetComponent implements OnInit {
    addAssetForm!: FormGroup;
    isSubmitting = false;
    selectedAssetType: string = '';
    selectedAsset: string = '';
    currencyOptions = Object.values(Currency);
    errorMessage: string = '';

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
            this.selectedAsset = uri;
            this.addAssetForm.get('uri')?.setValue(uri);
        })
    }

    onSubmit(): void {
        if (this.addAssetForm.invalid || !this.selectedAsset) {
            this.errorMessage = 'Please select an asset.';
            return;
        }
        this.isSubmitting = true;

        const asset = {
            assetType: this.selectedAssetType,
            uri: this.selectedAsset,
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
