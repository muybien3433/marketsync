import { Component } from '@angular/core';
import { WalletFooterNavbarComponent } from '../wallet-footer-navbar/wallet-footer-navbar.component';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import {environment} from '../../../../environments/environment';
import {API_ENDPOINTS} from '../../../services/api-endpoints';
import {AssetHistory} from "../../../models/asset-history";
import {CurrencyType} from "../../../models/currency-type";

@Component({
  selector: 'app-wallet-edit-asset',
  standalone: true,
  imports: [
    WalletFooterNavbarComponent,
    FormsModule,
    NgIf,
    ReactiveFormsModule,
    TranslatePipe,
    NgForOf
  ],
  templateUrl: './wallet-edit-asset.component.html',
  styleUrls: ['./wallet-edit-asset.component.css']
})
export class WalletEditAssetComponent {
  editAssetForm!: FormGroup;
  isSubmitting = false;
  errorMessage: string = '';
  assetId: number | null = null;
  currencyOptions = Object.values(CurrencyType);

  constructor(
      private fb: FormBuilder,
      private http: HttpClient,
      private router: Router
  ) {
    const navigation = this.router.getCurrentNavigation();
    const asset = navigation?.extras.state?.['asset'] as AssetHistory;

    if (asset) {
      this.assetId = asset.id;
      this.editAssetForm = this.fb.group({
        assetType: [{ value: asset.assetType, disabled: true }],
        uri: [{ value: asset.name, disabled: true }],
        count: [asset.count, [Validators.required, Validators.min(0.001)]],
        purchasePrice: [asset.purchasePrice, [Validators.required, Validators.min(0.01)]],
        currency: [asset.currencyType, [Validators.required, Validators.minLength(3)]],
      });
    }
  }

  onSubmit(): void {
    if (this.editAssetForm.invalid || this.assetId === null) {
      return;
    }

    this.isSubmitting = true;

    const assetData = {
      assetType: this.editAssetForm.get('assetType')?.value,
      uri: this.editAssetForm.get('uri')?.value,
      count: this.editAssetForm.get('count')?.value,
      purchasePrice: this.editAssetForm.get('purchasePrice')?.value,
      currency: this.editAssetForm.get('currency')?.value,
    };

    this.editAsset(assetData)?.subscribe({
      next: () => {
        this.router.navigate(['wallet-asset-history']);
        this.isSubmitting = false;
      },
      error: () => {
        this.errorMessage = 'Failed to update asset.';
        this.isSubmitting = false;
      }
    });
  }

  editAsset(assetData: { assetType: string; uri: string; count: number; purchasePrice: number; currency: string }) {
    if (this.assetId === null) {
      this.errorMessage = 'Invalid asset ID.';
      return;
    }
    return this.http.patch(`${environment.baseUrl}${API_ENDPOINTS.WALLET}/${this.assetId}`, assetData);
  }
}