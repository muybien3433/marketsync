import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';
import {AssetSelectionListComponent} from "../../asset-selection-list/asset-selection-list.component";
import {Currency} from "../../../models/currency";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {environment} from "../../../../environments/environment";
import {API_ENDPOINTS} from "../../../services/api-endpoints";
import {AssetService} from "../../../services/asset.service";
import {NgForOf, NgIf} from "@angular/common";
import {AssetAggregate} from "../../../models/asset-aggregate";

@Component({
    selector: 'app-add-subscription',
    templateUrl: './add-subscription.component.html',
    standalone: true,
    imports: [
        AssetSelectionListComponent,
        TranslatePipe,
        ReactiveFormsModule,
        NgForOf,
        NgIf
    ],
    styleUrls: ['./add-subscription.component.css']
})
export class AddSubscriptionComponent implements OnInit {
    addSubscriptionForm: FormGroup;
    selectedAssetType: string = '';
    selectedAsset: any = null;
    uri: string = '';
    currencyOptions = Object.values(Currency);
    isSubmitting = false;
    errorMessage: string = '';
    assets: AssetAggregate[] = [];

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
            currencyType: ['USD', [Validators.required]],
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
        if (asset && asset.uri) {
            this.selectedAsset = asset;
            this.addSubscriptionForm.get('uri')?.setValue(asset.uri);
        } else {
            this.errorMessage = 'Please select a valid asset.';
        }
    }

    onSubmit() {
        if (this.addSubscriptionForm.invalid || !this.uri) {
            this.errorMessage = 'Please select an asset.';
            return;
        }
        this.isSubmitting = true;

        const formValue = this.addSubscriptionForm.value;
        const condition = formValue.condition;
        const numericValue = parseFloat(formValue.value);

        const subscription = {
            uri: this.uri,
            assetType: this.selectedAssetType,
            currencyType: formValue.currency,
            notificationType: formValue.notificationType,
            upperBoundPrice: condition === 'increase' ? numericValue : null,
            lowerBoundPrice: condition === 'decrease' ? numericValue : null
        };

        console.log('Subscription payload:', subscription);

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