import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import {TranslatePipe} from '@ngx-translate/core';
import {WalletFooterNavbarComponent} from '../wallet-footer-navbar/wallet-footer-navbar.component';
import {NgIf} from '@angular/common';
import {
  AssetSelectionComponent
} from '../asset-selection-list/asset-selection/asset-selection.component';
import {AssetService} from '../../../services/asset-service';
import {environment} from '../../../../environments/environment.development';
import {API_ENDPOINTS} from '../../../services/api-endpoints';

@Component({
  selector: 'app-add-asset',
  standalone: true,
  imports: [
    FormsModule,
    TranslatePipe,
    WalletFooterNavbarComponent,
    NgIf,
    AssetSelectionComponent,
    ReactiveFormsModule
  ],
  templateUrl: './add-asset.component.html',
  styleUrls: ['./add-asset.component.css'],
})
export class AddAssetComponent implements OnInit {
  addAssetForm!: FormGroup;
  assetListForm!: FormGroup;
  isSubmitting = false;
  successMessage: string = '';
  errorMessage: string = '';
  assetTypes: { type: string, label: string }[] = [];
  filteredAssetTypes: { type: string, label: string }[] = [];
  selectedAssetUri: string = '';

  constructor(private fb: FormBuilder, private http: HttpClient, private assetService: AssetService) {
    this.addAssetForm = this.fb.group({
      type: ['crypto', Validators.required],
      uri: ['', [Validators.required, Validators.minLength(1)]],
      count: [0, [Validators.required, Validators.min(0.001)]],
      purchasePrice: [0, [Validators.required, Validators.min(0.01)]],
    });

    this.assetListForm = this.fb.group({
      type: [''],
    });
  }

  ngOnInit(): void {
    this.assetService.assetUri$.subscribe(uri => {
      this.selectedAssetUri = uri;
      this.addAssetForm.get('uri')?.setValue(uri);
    })
    this.filteredAssetTypes = this.assetTypes;
  }

  onSubmit(): void {
    if (this.addAssetForm.invalid) {
      return;
    }

    this.isSubmitting = true;

    const assetData = {
      type: this.addAssetForm.get('type')?.value?.toUpperCase(), // needs to be upper case to satisfy java's enum
      uri: this.addAssetForm.get('uri')?.value,
      count: this.addAssetForm.get('count')?.value,
      purchasePrice: this.addAssetForm.get('purchasePrice')?.value,
    };

    this.addAsset(assetData)?.subscribe({
      next: () => {
        this.successMessage = 'Asset added successfully.';
        this.isSubmitting = false;
      },
      error: () => {
        this.errorMessage = 'Failed to add asset.';
        this.isSubmitting = false;
      }
    });
  }

  addAsset(assetData: { type: string; uri: string; count: number; purchasePrice: number }) {
    return this.http.post(`${environment.baseUrl}${API_ENDPOINTS.ASSET}`, assetData);
  }
}
