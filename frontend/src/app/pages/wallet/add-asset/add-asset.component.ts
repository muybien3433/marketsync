import { Component } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {NgIf} from '@angular/common';
import {TranslatePipe} from '@ngx-translate/core';
import {WalletFooterNavbarComponent} from '../wallet-footer-navbar/wallet-footer-navbar.component';

@Component({
  selector: 'app-add-asset',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    NgIf,
    TranslatePipe,
    WalletFooterNavbarComponent
  ],
  templateUrl: './add-asset.component.html',
  styleUrl: './add-asset.component.css'
})

export class AddAssetComponent {
  addAssetForm: FormGroup;
  isSubmitting = false;
  successMessage: string = '';
  errorMessage: string = '';

  private baseUrl = 'http://localhost:9999/api/v1/wallets';

constructor(private fb: FormBuilder, private http: HttpClient) {
    this.addAssetForm = this.fb.group({
      type: ['', Validators.required],
      uri: ['', [Validators.required, Validators.minLength(1)]],
      count: [0, [Validators.required, Validators.min(0.001)]],
      purchasePrice: [0, [Validators.required, Validators.min(0.01)]],
    });
  }

  addAssetToWallet(assetData: { type: string; uri: string; count: number; purchasePrice: number }): Observable<any> {
    return this.http.post(`${this.baseUrl}/assets`, assetData);
  }

  onSubmit(): void {
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
        console.error();
        this.isSubmitting = false;
      },
    });
  }
}
