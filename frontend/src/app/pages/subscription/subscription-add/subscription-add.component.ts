import {Component, OnDestroy, OnInit} from '@angular/core';
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
import {CurrencyService} from "../../../services/currency-service";
import {Subscription} from "rxjs";
import {CurrencyType} from "../../../models/currency-type";
import {AssetType} from "../../../models/asset-type";
import {SubscriptionFooterNavbarComponent} from "../subscription-footer-navbar/subscription-footer-navbar.component";

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
        CurrencyChangeOptionComponent,
        SubscriptionFooterNavbarComponent
    ],
    styleUrls: ['./subscription-add.component.css']
})
export class SubscriptionAddComponent implements OnInit, OnDestroy {
    addSubscriptionForm: FormGroup;
    isSubmitting = false;
    errorMessage: string = '';

    private assetTypeSubscription!: Subscription;
    assetType!: AssetType;

    protected assetSubscription!: Subscription;
    asset: AssetDetail | null = null;

    private currencyTypeSubscription!: Subscription;
    currencyType!: CurrencyType;

    constructor(
        private fb: FormBuilder,
        private http: HttpClient,
        private router: Router,
        private assetService: AssetService,
        private currencyService: CurrencyService,
    ) {
        this.addSubscriptionForm = this.fb.group({
            uri: ['', [Validators.required, Validators.minLength(1)]],
            assetType: ['', Validators.required],
            notificationType: ['EMAIL', Validators.required],
            currencyType: [currencyService.getSelectedCurrencyType(), [Validators.required]],
            condition: ['increase', Validators.required],
            value: ['0.01', [Validators.required, Validators.min(0)]],
        });
    }

    ngOnInit(): void {
        this.assetTypeSubscription = this.assetService.selectedAssetType$.subscribe(assetType => {
            this.assetType = assetType;
            this.addSubscriptionForm.get('assetType')?.setValue(assetType);
        })
        this.assetSubscription = this.assetService.selectedAsset$.subscribe(asset => {
            this.asset = asset;
            this.addSubscriptionForm.get('uri')?.setValue(asset?.uri);
        })
        this.currencyTypeSubscription = this.currencyService.selectedCurrencyType$.subscribe(currencyType => {
            this.currencyType = currencyType;
            this.addSubscriptionForm.get('currency')?.setValue(currencyType);
        })
    }

    ngOnDestroy(): void {
        this.assetTypeSubscription.unsubscribe();
        this.assetSubscription.unsubscribe();
        this.currencyTypeSubscription.unsubscribe();
    }

    onAssetChange(asset: AssetDetail) {
        this.asset = asset;
        this.addSubscriptionForm.get('uri')?.setValue(asset ? asset.uri : '');
    }

    onSubmit() {
        if (this.isSubmitting) {
            return;
        }

        if (this.addSubscriptionForm.invalid) {
            this.errorMessage = 'Please select an asset.';
            return;
        }

        this.isSubmitting = true;
        this.errorMessage = '';

        const formValue = this.addSubscriptionForm.value;
        const condition = formValue.condition;
        const numericValue = parseFloat(formValue.value);
        const subscription = {
            assetType: formValue.assetType,
            uri: formValue.uri,
            currencyType: formValue.currencyType,
            notificationType: formValue.notificationType,
            upperBoundPrice: condition === 'increase' ? numericValue : null,
            lowerBoundPrice: condition === 'decrease' ? numericValue : null
        };

        if (condition === 'increase' && this.asset && numericValue < this.asset.price) {
            this.errorMessage = 'Value must be above current price';
            this.isSubmitting = false;
            return;
        }

        if (condition === 'decrease' && this.asset && numericValue > this.asset.price) {
            this.errorMessage = 'Value must be below current price';
            this.isSubmitting = false;
            return;
        }

        this.addSubscription(subscription)?.subscribe({
            next: () => {
                this.isSubmitting = false;
                this.router.navigate(['subscription']);
            },
            error: () => {
                this.isSubmitting = false;
                this.errorMessage = 'Failed to add subscription.';
            }
        });
    }

    private addSubscription(subscription: {
        uri: string;
        assetType: AssetType;
        currencyType: CurrencyType;
        notificationType: string;
        upperBoundPrice: number | null;
        lowerBoundPrice: number | null;
    }) {
        return this.http.post(
            `${environment.baseUrl}${API_ENDPOINTS.SUBSCRIPTION}`,
            subscription);
    }
}