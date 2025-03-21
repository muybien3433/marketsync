import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';
import {AssetSelectionListComponent} from "../../common/asset-selection-list/asset-selection-list.component";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {environment} from "../../../../environments/environment";
import {API_ENDPOINTS} from "../../../services/api-endpoints";
import {AssetService} from "../../../services/asset.service";
import {NgIf} from "@angular/common";
import {AssetDetail} from "../../../models/asset-detail";
import {AssetPriceDisplayComponent} from "../../common/asset-price-display/asset-price-display.component";
import {CurrencyChangeOptionComponent} from "../../common/currency-change-option/currency-change-option.component";

@Component({
    selector: 'app-subscription-add',
    templateUrl: './subscription-add.component.html',
    standalone: true,
    imports: [
        AssetSelectionListComponent,
        TranslatePipe,
        ReactiveFormsModule,
        NgIf,
        AssetPriceDisplayComponent,
        CurrencyChangeOptionComponent
    ],
    styleUrls: ['./subscription-add.component.css']
})
export class SubscriptionAddComponent implements OnInit {
    @Input() selectedCurrency: string = '';
    addSubscriptionForm: FormGroup;
    selectedAssetType: string = '';
    selectedAsset: AssetDetail | undefined;
    uri: string = '';
    isSubmitting = false;
    errorMessage: string = '';
    assets: AssetDetail[] = [];

    constructor(
        private fb: FormBuilder,
        private http: HttpClient,
        private router: Router,
        private assetService: AssetService,
    ) {
        this.addSubscriptionForm = this.fb.group({
            uri: ['', [Validators.required, Validators.minLength(1)]],
            assetType: ['', Validators.required],
            notificationType: ['EMAIL', Validators.required],
            currencyType: ['', [Validators.required]],
            condition: ['increase', Validators.required],
            value: ['0.01', [Validators.required, Validators.min(0)]],
        });
    }

    ngOnInit(): void {
        this.assetService.selectedAssetType$.subscribe(assetType => {
            this.selectedAssetType = assetType;
            this.addSubscriptionForm.get('assetType')?.setValue(assetType);
        })
        this.assetService.selectedAssetUri$.subscribe(uri => {
            this.uri = uri;
            this.addSubscriptionForm.get('uri')?.setValue(uri);
        });
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
        this.addSubscriptionForm.get('uri')?.setValue('');
    }

    onCurrencySelected(selectedCurrency: any) {
        this.addSubscriptionForm.get('currencyType')?.setValue(selectedCurrency);
    }

    onSubmit() {
        if (this.addSubscriptionForm.invalid) {
            this.errorMessage = 'Please select an asset.';
            return;
        }

        this.isSubmitting = true;
        this.errorMessage = '';

        const formValue = this.addSubscriptionForm.value;
        const condition = formValue.condition;
        const numericValue = parseFloat(formValue.value);

        if (!this.selectedAsset?.price) {
            this.errorMessage = 'Could not verify current asset price';
            this.isSubmitting = false;
            return;
        }

        const subscription = {
            assetType: this.selectedAssetType,
            uri: this.selectedAsset?.uri,
            currencyType: formValue.currencyType,
            notificationType: formValue.notificationType,
            upperBoundPrice: condition === 'increase' ? numericValue : null,
            lowerBoundPrice: condition === 'decrease' ? numericValue : null
        };

        if (condition === 'increase' && numericValue < this.selectedAsset.price) {
            this.errorMessage = 'Value must be above current price';
            this.isSubmitting = false;
            return;
        }

        if (condition === 'decrease' && numericValue > this.selectedAsset.price) {
            this.errorMessage = 'Value must be below current price';
            this.isSubmitting = false;
            return;
        }

        this.addSubscription(subscription)?.subscribe({
            next: () => {
                this.isSubmitting = false;
                this.router.navigate(['wallet']);
            },
            error: () => {
                this.isSubmitting = false;
                this.errorMessage = 'Failed to add subscription.';
            }
        });
    }

    private addSubscription(subscription: {
        uri: string;
        assetType: string;
        currencyType: string;
        notificationType: string;
        upperBoundPrice: number | null;
        lowerBoundPrice: number | null;
    }) {
        return this.http.post(
            `${environment.baseUrl}${API_ENDPOINTS.SUBSCRIPTION}`,
            subscription);
    }
}