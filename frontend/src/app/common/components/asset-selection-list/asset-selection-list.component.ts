import {Component, EventEmitter, Input, OnDestroy, OnInit, Output,} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TranslatePipe, TranslateService} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {FilterByNamePipe} from "../../service/filter-by-name-pipe";
import {AssetDetail} from "../../model/asset-detail.model";
import {AssetType} from "../../enum/asset-type";
import {CurrencyType} from "../../enum/currency-type";
import {AssetService} from "../../service/asset-service";
import {CurrencyService} from "../../service/currency-service";
import { UnitType } from '../../enum/unit-type';
import {AssetBase} from "../../model/asset-base.model";
import {LoadingSpinnerComponent} from "../loading/loading-spinner.component";

@Component({
    selector: 'app-asset-selection-list',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        TranslatePipe,
        FilterByNamePipe,
        FormsModule,
        LoadingSpinnerComponent,
    ],
    templateUrl: './asset-selection-list.component.html',
    styleUrl: './asset-selection-list.component.scss'
})
export class AssetSelectionListComponent implements OnInit, OnDestroy {
    @Input() assetTypeOptions: AssetType[] = Object.values(AssetType).filter(type => type !== AssetType.CURRENCY);
    @Output() assetChanged: EventEmitter<AssetDetail> = new EventEmitter();

    protected readonly AssetType = AssetType;

    _assets: AssetBase[] = [];
    filteredAssets: AssetBase[] = [];
    searchTerm = '';
    selectedAsset: AssetDetail | null = null;
    selectedAssetType: AssetType = AssetType.CRYPTO;
    assetTypeDropdownOptions: { label: string; value: AssetType }[] = [];
    customAssetName: string = '';
    _currencies: CurrencyType[] = Object.values(CurrencyType).filter(value => typeof value === 'string') as CurrencyType[];
    CurrencyType = CurrencyType;

    private currencySubscription!: Subscription;
    currentCurrency!: CurrencyType;

    isLoading: boolean = false;

    constructor(
        private assetService: AssetService,
        private currencyService: CurrencyService,
        private translate: TranslateService
    ) {
    }

    ngOnInit(): void {
        this.currencySubscription = this.currencyService.selectedCurrencyType$
            .subscribe(currency => this.currentCurrency = currency);

        this.buildAssetTypeOptions();
        this.translate.onLangChange.subscribe(() => this.buildAssetTypeOptions());

        this.fetchAssets();

        this.assetService.selectedAsset$.subscribe(asset => {
            this.selectedAsset = asset;
            if (asset) {
                this.assetChanged.emit(asset);
            }
        });
    }

    ngOnDestroy(): void {
        this.currencySubscription.unsubscribe();
        this.resetPickedAsset();
    }

    private buildAssetTypeOptions(): void {
        const keys = this.assetTypeOptions.map(type => 'asset.type.' + type.toLowerCase());

        this.translate.get(keys).subscribe(translations => {
            this.assetTypeDropdownOptions = this.assetTypeOptions.map(type => {
                const key = 'asset.type.' + type.toLowerCase();
                return {
                    label: translations[key],
                    value: type
                };
            });
        });
    }

    fetchAssets(): void {
        this.isLoading = true;
        this.assetService.getAssetsBaseByAssetType(this.selectedAssetType).subscribe({
            next: (assets: AssetBase[]) => {
                this._assets = assets;
                this.filteredAssets = assets.slice(0, 200);
                this.isLoading = false;
            },
            error: err => {
                console.error('Error fetching assets for type: ', this.selectedAssetType, err);
                this._assets = [];
                this.filteredAssets = [];
                this.isLoading = false;
            }
        });
    }

    onAssetTypeChange(assetType: AssetType): void {
        this.selectedAssetType = assetType;
        this.assetService.setSelectedAssetType(assetType);
        this.resetPickedAsset();

        this._assets = [];
        this.filteredAssets = [];

        if (assetType !== AssetType.CUSTOM && assetType !== AssetType.CURRENCY) {
            this.fetchAssets();
        }
    }

    onAssetSelect(assetBase: AssetBase): void {
        if (!assetBase?.uri) {
            console.error("AssetBase without uri", assetBase);
            return;
        }

        this.assetService.setSelectedAssetRef(this.selectedAssetType, assetBase.uri);
    }

    private normalizeUnitType(v: any): UnitType | null {
        if (v === undefined || v === null) return UnitType.UNIT;
        if (typeof v === 'string' && v.trim() === '') return UnitType.UNIT;
        return v as UnitType;
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
        this.assetChanged.emit(currencyAsset);
    }

    onSearchTermChange(term: string): void {
        this.searchTerm = term;
        const t = term.toLowerCase().trim();

        if (!t) {
            this.filteredAssets = this._assets.slice(0, 200);
            return;
        }

        this.filteredAssets = this._assets
            .filter(a =>
                (a.name && a.name.toLowerCase().includes(t)) ||
                (a.symbol && a.symbol.toLowerCase().includes(t))
            )
            .slice(0, 200);
    }

    trackByAsset(index: number, item: AssetBase): string {
        return item.uri;
    }

    resetPickedAsset() {
        this.searchTerm = '';
        this.customAssetName = '';
        this.selectedAsset = null;
        this.assetService.setSelectedAsset(null);
        this.assetChanged.emit(null);
    }
}
