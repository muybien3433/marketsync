import {Component, EventEmitter, OnDestroy, OnInit, Output,} from '@angular/core';
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TranslatePipe} from "@ngx-translate/core";
import {FilterByNamePipe} from "../../../services/filter-by-name-pipe";
import {HttpClient} from "@angular/common/http";
import {AssetService} from "../../../services/asset.service";
import {environment} from "../../../../environments/environment";
import {API_ENDPOINTS} from "../../../services/api-endpoints";
import {catchError, map, of, Subscription} from "rxjs";
import {AssetType} from "../../../models/asset-type";
import {AssetDetail} from "../../../models/asset-detail";
import {CurrencyType} from "../../../models/currency-type";
import {CurrencyService} from "../../../services/currency-service";

@Component({
    selector: 'app-asset-selection-list',
    standalone: true,
    imports: [
        MatFormField,
        MatLabel,
        MatOption,
        MatSelect,
        NgForOf,
        NgIf,
        ReactiveFormsModule,
        TranslatePipe,
        FilterByNamePipe,
        FormsModule,
    ],
    templateUrl: './asset-selection-list.component.html',
    styleUrl: './asset-selection-list.component.css'
})
export class AssetSelectionListComponent implements OnInit, OnDestroy {
    @Output() assetChanged: EventEmitter<AssetDetail> = new EventEmitter();

    _assets: AssetDetail[] = [];
    selectedAssetType: AssetType = AssetType.CRYPTO;
    selectedAsset!: AssetDetail | null;
    assetTypeOptions: AssetType[] = Object.values(AssetType);
    searchTerm: string = '';

    private currencySubscription!: Subscription;
    currentCurrency!: CurrencyType;

    constructor(
        private http: HttpClient,
        private assetService: AssetService,
        private currencyService: CurrencyService,
    ) {
    }

    ngOnInit(): void {
        this.currencySubscription = this.currencyService.selectedCurrencyType$.subscribe(currency => {
            this.currentCurrency = currency;
            this.fetchAssets(AssetType.CRYPTO, currency);
        })
    }

    ngOnDestroy(): void {
        this.currencySubscription.unsubscribe();
    }

    onAssetTypeSelect(assetType: AssetType): void {
        this.assetService.setSelectedAssetType(assetType);
        this.resetPickedAsset();
        this.fetchAssets(assetType, this.currentCurrency);
    }

    onAssetSelect(asset: AssetDetail) {
        this.selectedAsset = asset;
        this.assetService.setSelectedAsset(asset);
        this.assetChanged.emit(asset);
    }

    resetPickedAsset() {
        this.searchTerm = '';
        this.selectedAsset = null;
        this.assetService.setSelectedAsset(null);
        this.assetChanged.emit(undefined);
    }

    fetchAssets(assetType: AssetType, currencyType: CurrencyType): void {
        this.http
            .get<any[]>(`${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${assetType}/currencies/${currencyType}`)
            .pipe(
                map((data) => {
                    this._assets = Object.values(data);

                    const currentAsset = this.assetService.getSelectedAsset();
                    if (currentAsset) {
                        const updatedAsset =
                            this._assets.find(a => a.uri === currentAsset.uri);
                        if (updatedAsset) {
                            this.assetService.setSelectedAsset(updatedAsset);
                        } else {
                            this.resetPickedAsset();
                        }
                    }
                }),
                catchError((error) => {
                    console.error('Error fetching assets: ', error);
                    this._assets = [];
                    this.resetPickedAsset();
                    return of([]);
                })
            )
            .subscribe();
    }
}
