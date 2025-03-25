import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {environment} from "../../../../environments/environment";
import {NgIf} from "@angular/common";
import {Subscription} from "rxjs";
import {
    AssetSelectionListComponent
} from "../../../common/components/asset-selection-list/asset-selection-list.component";
import {AssetPriceDisplayComponent} from "../../../common/components/asset-price-display/asset-price-display.component";
import {
    CurrencyChangeOptionComponent
} from "../../../common/components/currency-change-option/currency-change-option.component";
import {AssetType} from "../../../common/model/asset-type";
import {AssetDetail} from "../../../common/model/asset-detail";
import {CurrencyType} from "../../../common/model/currency-type";
import {AssetService} from "../../../common/service/asset-service";
import {CurrencyService} from "../../../common/service/currency-service";
import {API_ENDPOINTS} from "../../../common/service/api-endpoints";
import {CardComponent} from "../../../common/components/card/card.component";
import {NumberInputDirective} from "../../../common/service/number-input.directive";

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
        CardComponent,
        NumberInputDirective
    ],
    styleUrls: ['./subscription-add.component.scss']
})
export default class SubscriptionAddComponent implements OnInit, OnDestroy {
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

        const formValue = this.addSubscriptionForm.getRawValue();
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
                this.router.navigate(['subscription/subscriptions']);
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

    protected readonly Event = Event;
    protected readonly event = event;
}