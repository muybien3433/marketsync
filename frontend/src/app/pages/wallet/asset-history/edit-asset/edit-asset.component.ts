import { Component } from '@angular/core';
import { WalletFooterNavbarComponent } from '../../wallet-footer-navbar/wallet-footer-navbar.component';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgIf } from '@angular/common';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Asset } from '../../asset-model';
import {environment} from '../../../../../environments/environment.development';
import {API_ENDPOINTS} from '../../../../services/api-endpoints';

@Component({
  selector: 'app-edit-asset',
  standalone: true,
  imports: [
    WalletFooterNavbarComponent,
    FormsModule,
    NgIf,
    ReactiveFormsModule,
    TranslatePipe
  ],
  templateUrl: './edit-asset.component.html',
  styleUrls: ['./edit-asset.component.css']
})
export class EditAssetComponent {
  editAssetForm: FormGroup;
  isSubmitting = false;
  successMessage: string = '';
  errorMessage: string = '';
  assetId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private translate: TranslateService
  ) {
    const navigation = this.router.getCurrentNavigation();
    const asset = navigation?.extras.state?.['asset'] as Asset;

    if (asset) {
      this.assetId = asset.id;
      this.editAssetForm = this.fb.group({
        originalType: [asset.type],
        type: [{ value: '', disabled: true }],
        uri: [{ value: asset.name, disabled: true }],
        count: [asset.count, [Validators.required, Validators.min(0.001)]],
        purchasePrice: [asset.averagePurchasePrice, [Validators.required, Validators.min(0.01)]],
      });

      this.translate
        .get(`asset.type.${asset.type}`)
        .subscribe(translatedType => {
          this.editAssetForm.get('type')?.setValue(translatedType);
        });
    } else {
      this.errorMessage = 'No asset data provided. Redirecting back.';
      this.editAssetForm = this.fb.group({
        originalType: [''],
        type: [{ value: '', disabled: true }],
        uri: [{ value: '', disabled: true }],
        count: [0, [Validators.required, Validators.min(0.01)]],
        purchasePrice: [0, [Validators.required, Validators.min(0.01)]],
      });
    }
  }

  onSubmit(): void {
    if (this.editAssetForm.invalid || this.assetId === null) {
      return;
    }

    this.isSubmitting = true;

    const assetData = {
      type: this.editAssetForm.get('originalType')?.value?.toUpperCase(),
      uri: this.editAssetForm.get('uri')?.value,
      count: this.editAssetForm.get('count')?.value,
      purchasePrice: this.editAssetForm.get('purchasePrice')?.value,
    };

    this.editAsset(assetData)?.subscribe({
      next: () => {
        this.successMessage = 'Asset updated successfully.';
        this.isSubmitting = false;
      },
      error: () => {
        this.errorMessage = 'Failed to update asset.';
        this.isSubmitting = false;
      }
    });
  }

  editAsset(assetData: { type: string; uri: string; count: number; purchasePrice: number }) {
    if (this.assetId === null) {
      this.errorMessage = 'Invalid asset ID.';
      return;
    }
    return this.http.put(`${environment.baseUrl}${API_ENDPOINTS.WALLET}/${this.assetId}`, assetData);
  }
}
