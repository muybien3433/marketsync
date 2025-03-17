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
    currencyOptions = Object.values(Currency);
    isSubmitting = false;
    errorMessage: string = '';

    constructor(
        private fb: FormBuilder,
        private http: HttpClient,
        private router: Router,
        private assetService: AssetService,
    ) {
        this.addSubscriptionForm = this.fb.group({
            notificationType: ['EMAIL', Validators.required],
            assetType: ['', Validators.required],
            uri: ['', [Validators.required, Validators.minLength(1)]],
            condition: ['increase', Validators.required],
            value: ['0.01', [Validators.required, Validators.min(0)]],
            currency: ['USD', [Validators.required]],
        });
    }

    ngOnInit(): void {
        this.assetService.selectedAssetType$.subscribe(assetType => {
            this.selectedAssetType = assetType;
            this.addSubscriptionForm.get('assetType')?.setValue(assetType);
        })
        this.assetService.selectedAssetUri$.subscribe(uri => {
            this.selectedAsset = uri;
            this.addSubscriptionForm.get('uri')?.setValue(uri);
        })
    }

    onSubmit() {
        if (this.addSubscriptionForm.invalid || !this.selectedAsset) {
            this.errorMessage = 'Please select an asset.';
            return;
        }
        this.isSubmitting = true;

        const subscription = {
            uri: this.selectedAsset,
            value: this.addSubscriptionForm.get('value')?.value,
            assetType: this.selectedAssetType,
            currency: this.addSubscriptionForm.get('currency')?.value,
            notificationType: this.addSubscriptionForm.get('notificationType')?.value,
            condition: this.addSubscriptionForm.get('condition')?.value,
        }

        console.log(subscription.notificationType);
        console.log(subscription.assetType);
        console.log(subscription.uri);
        console.log(subscription.value);
        console.log(subscription.currency);
        console.log(subscription.condition);

        this.addSubscription(subscription)?.subscribe({
            next: () => {
                this.isSubmitting = false;
                this.router.navigate(['subscription']);
            },
            error: () => {
                this.isSubmitting = false;
                this.errorMessage = 'Failed to add asset.';
            }
        });
    }

    private addSubscription(subscription: {
        uri: string;
        condition: string;
        value: number;
        assetType: string
        currency: string;
        notificationType: string;
    }) {
        return this.http.post(
            `${environment.baseUrl}/${API_ENDPOINTS.SUBSCRIPTION}/${subscription.condition}`,
            subscription);
    }
}