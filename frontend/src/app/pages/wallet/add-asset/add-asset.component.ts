import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {TranslatePipe} from '@ngx-translate/core';
import {WalletFooterNavbarComponent} from '../wallet-footer-navbar/wallet-footer-navbar.component';
import {NgIf} from '@angular/common';
import {
  AssetSelectionComponent
} from '../asset-selection-list/asset-selection/asset-selection/asset-selection.component';
import {AssetService} from '../../../services/asset-service';

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

  private baseUrl = 'http://localhost:9999/api/v1';

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

  addAssetToWallet(assetData: { type: string; uri: string; count: number; purchasePrice: number }): Observable<any> {
    assetData.type.toUpperCase(); // need to be upper case to satisfy java enum
    return this.http.post(`${this.baseUrl}/wallets/assets`, assetData);
  }

  onSubmit(): void {
    console.log("Form Validity: ", this.addAssetForm.valid);
    console.log("Form control validity: ", {
      count: this.addAssetForm.get('count')?.valid,
      purchasePrice: this.addAssetForm.get('purchasePrice')?.valid,
      type: this.addAssetForm.get('type')?.valid,
      uri: this.addAssetForm.get('uri')?.valid
    });

    if (this.addAssetForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    const assetData = this.addAssetForm.value;

    this.addAssetToWallet(assetData).subscribe({
      next: () => {
        this.successMessage = 'Asset added successfully!';
        this.errorMessage = '';
        this.addAssetForm.reset();
        this.isSubmitting = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to add asset. Please try again.';
        this.successMessage = '';
        console.error(err);
        this.isSubmitting = false;
      },
    });
  }
}
