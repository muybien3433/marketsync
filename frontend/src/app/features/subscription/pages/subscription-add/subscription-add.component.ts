import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {NgIf} from "@angular/common";
import {Subscription} from "rxjs";
import {
    AssetSelectionListComponent
} from "../../../wallet/components/asset-selection-list/asset-selection-list.component";
import {AssetPriceDisplayComponent} from "../../../wallet/components/asset-price-display/asset-price-display.component";
import {
    CurrencyChangeOptionComponent
} from "../../../../shared/components/currency-change-option/currency-change-option.component";
import {AssetType} from "../../../../shared/enum/asset-type";
import {AssetDetail} from "../../../wallet/models/asset-detail.model";
import {CurrencyType} from "../../../../shared/enum/currency-type";
import {API_ENDPOINTS} from "../../../../core/http/api/api-endpoints";
import {CardComponent} from "../../../../shared/ui/card/card.component";
import {NumberInputDirective} from "../../../../shared/form/number-input/number-input.directive";
import {WalletApi} from "../../../wallet/data-access/wallet.api";
import {CurrencyService} from "../../../../core/services/currency.service";

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
    private readonly fb = inject(FormBuilder);
    private readonly http = inject(HttpClient);
    private readonly router = inject(Router);
    private readonly assetService = inject(WalletApi);
    private readonly currencyService = inject(CurrencyService);
    private readonly translate = inject(TranslateService);

    addSubscriptionForm: FormGroup;
    isSubmitting = false;
    errorMessage: string = '';

    filteredAssetTypes: AssetType[] = Object.values(AssetType).filter(t => t !== AssetType.CUSTOM && t !== AssetType.CURRENCY);

    private assetTypeSubscription!: Subscription;
    assetType!: AssetType;

    protected assetSubscription!: Subscription;
    asset: AssetDetail | null = null;

    private currencyTypeSubscription!: Subscription;
    currencyType!: CurrencyType;

    constructor() {
        this.addSubscriptionForm = this.fb.group({
            uri: ['', [Validators.required, Validators.minLength(1)]],
            assetType: ['', Validators.required],
            notificationType: ['EMAIL', Validators.required],
            currencyType: [this.currencyService.getGlobalCurrencyType(), [Validators.required]],
            condition: ['increase', Validators.required],
            value: ['0.01', [Validators.required, Validators.min(0.0000000000001)]],
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
            this.addSubscriptionForm.get('currencyType')?.setValue(currencyType);
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
            this.errorMessage = this.translate.instant('error.asset.is.required');
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
            this.errorMessage = this.translate.instant('error.subscription.value.must.be.above');
            this.isSubmitting = false;
            return;
        }

        if (condition === 'decrease' && this.asset && numericValue > this.asset.price) {
            this.errorMessage = this.translate.instant('error.subscription.value.must.be.below');
            this.isSubmitting = false;
            return;
        }

        console.log(this.assetType)

        this.addSubscription(subscription)?.subscribe({
            next: () => {
                this.isSubmitting = false;
                this.router.navigate(['subscription/subscriptions']);
            },
            error: () => {
                this.isSubmitting = false;
                this.errorMessage = this.translate.instant('error.subscription.add.fail');
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
            `${API_ENDPOINTS.SUBSCRIPTION}`,
            subscription);
    }

    protected readonly AssetType = AssetType;
}