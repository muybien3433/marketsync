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
  maxCommentLength: number = 200;
  remainingCommentChars: number = this.maxCommentLength;
  maxUnitLength: number = 6;
  remainingUnitChars: number = this.maxUnitLength;

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
        unitType: [ asset.unitType || '', [Validators.maxLength(this.maxUnitLength)]],
        uri: [{ value: asset.uri, disabled: asset.assetType !== 'CUSTOM' }],
        count: [asset.count, [Validators.required, Validators.min(0.0000000000001)]],
        purchasePrice: [asset.purchasePrice, [Validators.required, Validators.min(0.0000000000001)]],
        currentPrice: [asset.currentPrice, [Validators.min(0.0000000000001)]],
        currencyType: [asset.currencyType, [Validators.required]],
        comment: [asset.comment || '', [Validators.maxLength(this.maxCommentLength)]],
      });

      this.editAssetForm.get('assetType')?.valueChanges.subscribe(value => {
        if (value === 'CUSTOM') {
          this.editAssetForm.get('uri')?.enable();
        } else {
          this.editAssetForm.get('uri')?.disable();
        }
      });

      this.remainingCommentChars = this.maxCommentLength - (asset.comment?.length || 0);
      this.editAssetForm.get('comment')?.valueChanges.subscribe(value => {
        const length = value?.length || 0;
        this.remainingCommentChars = this.maxCommentLength - length;

        if (this.remainingCommentChars < 0) {
          const truncatedValue = value.substring(0, this.maxCommentLength);
          this.editAssetForm.get('comment')?.setValue(truncatedValue, { emitEvent: false });
          this.remainingCommentChars = 0;
        }
      });

      this.remainingUnitChars = this.maxUnitLength - (asset.unitType?.length || 0);
      this.editAssetForm.get('unitType')?.valueChanges.subscribe(value => {
        const length = value?.length || 0;
        this.remainingUnitChars = this.maxUnitLength - length;

        if (this.remainingUnitChars < 0) {
          const truncatedValue = value.substring(0, this.maxUnitLength);
          this.editAssetForm.get('unitType')?.setValue(truncatedValue, {emitEvent: false});
          this.remainingUnitChars = 0;
        }
      });
    } else {
      this.router.navigate(['wallet/assets/history'], { state: { asset } });
      this.editAssetForm = this.fb.group({
        assetType: [{ value: '', disabled: true }],
        unitType: [{ value: '', disabled: true }],
        uri: [{ value: '', disabled: true }],
        count: [0, [Validators.required, Validators.min(0.001)]],
        purchasePrice: [0, [Validators.required, Validators.min(0.01)]],
        currentPrice: [0, [Validators.required, Validators.min(0.01)]],
        currencyType: ['', [Validators.required]],
        comment: ['', [Validators.maxLength(this.maxCommentLength)]],
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
      unitType: formValue.unitType,
      uri: formValue.uri,
      count: formValue.count,
      purchasePrice: formValue.purchasePrice,
      currentPrice: formValue.currentPrice,
      currencyType: formValue.currencyType,
      comment: formValue.comment,
    };

    this.editAsset(assetData)?.subscribe({
      next: () => {
m        this.router.navigate(['wallet/assets/history']);
      },
      error: () => {
        this.errorMessage = 'Failed to update asset.';
        this.isSubmitting = false;
      }
    });
  }

  editAsset(assetData: { assetType: string; unitType: string; uri: string;
    count: number; purchasePrice: number; currentPrice: number, currencyType: string; comment: string }) {
    if (this.assetId === null) {
      this.errorMessage = 'Invalid asset ID.';
    }
    return this.http.patch(`${environment.baseUrl}${API_ENDPOINTS.WALLET}/${this.assetId}`, assetData);
  }
}