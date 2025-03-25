import {Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import {environment} from '../../../../environments/environment';
import {CurrencyType} from "../../../common/model/currency-type";
import {AssetHistory} from "../../../common/model/asset-history";
import {API_ENDPOINTS} from "../../../common/service/api-endpoints";
import {CardComponent} from "../../../common/components/card/card.component";
import {NumberInputDirective} from "../../../common/service/number-input.directive";

@Component({
  selector: 'app-wallet-edit-asset',
  standalone: true,
    imports: [
        FormsModule,
        NgIf,
        ReactiveFormsModule,
        TranslatePipe,
        CardComponent,
        NgForOf,
        NumberInputDirective
    ],
  templateUrl: './wallet-edit-asset.component.html',
  styleUrls: ['./wallet-edit-asset.component.scss']
})
export default class WalletEditAssetComponent {
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
        currency: [asset.currencyType, [Validators.required]]
      });
    } else {
      this.router.navigate(['wallet/assets/history'], { state: {asset} });
      this.editAssetForm = this.fb.group({
        assetType: [{ value: '', disabled: true }],
        uri: [{ value: '', disabled: true }],
        count: [0, [Validators.required, Validators.min(0.001)]],
        purchasePrice: [0, [Validators.required, Validators.min(0.01)]],
        currency: ['', [Validators.required]]
      });
    }
  }

  onSubmit(): void {
    if (this.editAssetForm.invalid || this.assetId === null) {
      return;
    }

    this.isSubmitting = true;

    const formValue = this.editAssetForm.getRawValue();
    const assetData = {
      assetType: formValue.assetType,
      uri: formValue.uri,
      count: formValue.count,
      purchasePrice: formValue.purchasePrice,
      currency: formValue.currency,
    };

    this.editAsset(assetData)?.subscribe({
      next: () => {
        this.router.navigate(['wallet/assets/history']);
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
    }
    return this.http.patch(`${environment.baseUrl}${API_ENDPOINTS.WALLET}/${this.assetId}`, assetData);
  }
}