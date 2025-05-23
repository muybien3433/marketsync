import {Component, EventEmitter, Input, OnDestroy, OnInit, Output,} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TranslatePipe} from "@ngx-translate/core";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environments/environment";
import {catchError, map, of, Subscription} from "rxjs";
import {FilterByNamePipe} from "../../service/filter-by-name-pipe";
import {AssetDetail} from "../../model/asset-detail";
import {AssetType} from "../../model/asset-type";
import {CurrencyType} from "../../model/currency-type";
import {AssetService} from "../../service/asset-service";
import {CurrencyService} from "../../service/currency-service";
import {API_ENDPOINTS} from "../../service/api-endpoints";
import { UnitType } from '../../model/unit-type';

@Component({
    selector: 'app-asset-selection-list',
    standalone: true,
    imports: [
        NgForOf,
        NgIf,
        ReactiveFormsModule,
        TranslatePipe,
        FilterByNamePipe,
        FormsModule,
    ],
    templateUrl: './asset-selection-list.component.html',
    styleUrl: './asset-selection-list.component.scss'
})
export class AssetSelectionListComponent implements OnInit, OnDestroy {
    @Input() assetTypeOptions: AssetType[] = Object.values(AssetType);
    @Output() assetChanged: EventEmitter<AssetDetail> = new EventEmitter();

    protected readonly AssetType = AssetType;

    _assets: AssetDetail[] = [];
    selectedAsset: AssetDetail | null = null;
    selectedAssetType: AssetType = AssetType.CRYPTO;
    searchTerm: string = '';
    customAssetName: string = '';
    _currencies: CurrencyType[] = Object.values(CurrencyType).filter(value => typeof value === 'string') as CurrencyType[];
    CurrencyType = CurrencyType;

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
            this.fetchAssets(this.selectedAssetType, currency);
        })
    }

    ngOnDestroy(): void {
        this.currencySubscription.unsubscribe();
        this.resetPickedAsset();
    }

    onAssetTypeSelect(assetType: AssetType): void {
        this.assetService.setSelectedAssetType(assetType);
        this.resetPickedAsset();

        this._assets = [];
        if (assetType !== AssetType.CUSTOM && assetType !== AssetType.CURRENCY) {
            this.fetchAssets(assetType, this.currentCurrency);
        }
    }

    onAssetSelect(asset: AssetDetail) {
        this.selectedAsset = asset;
        this.assetService.setSelectedAsset(this.selectedAsset);
        this.assetChanged.emit(this.selectedAsset);
    }
    
    onCustomAssetInput(): void {
        if (this.customAssetName.trim()) {
            const customAsset: AssetDetail = new AssetDetail(
                this.customAssetName,
                null,
                this.customAssetName,
                null,
                this.currentCurrency,
                AssetType.CUSTOM,
                UnitType.UNIT,
                null,
                null
            );
            this.selectedAsset = customAsset;
            this.assetService.setSelectedAsset(customAsset);
            this.assetChanged.emit(customAsset);
        } else {
            this.resetPickedAsset();
        }
    }
    
    onCurrencySelect(currency: CurrencyType) {
        const currencyAsset: AssetDetail = new AssetDetail(
            currency,
            CurrencyType.Symbols[currency],
            currency,
            null,
            currency,
            AssetType.CURRENCY,
            UnitType.UNIT,
            null,
            null
        );
        this.selectedAsset = currencyAsset;
        this.assetService.setSelectedAsset(currencyAsset);
        this.assetChanged.emit(this.selectedAsset);
    }

    resetPickedAsset() {
        this.searchTerm = '';
        this.customAssetName = '';
        this.selectedAsset = null;
        this.assetService.setSelectedAsset(null);
        this.assetChanged.emit(null);
    }

    fetchAssets(assetType: AssetType, currencyType: CurrencyType): void {
        if (assetType !== AssetType.CURRENCY && assetType !== AssetType.CUSTOM) {
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
}
