import {Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output,} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TranslatePipe, TranslateService} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {FilterByNamePipe} from "../../../../shared/pipes/filter-by-name-pipe";
import {AssetDetail} from "../../models/asset-detail.model";
import {AssetType} from "../../../../shared/enum/asset-type";
import {CurrencyType} from "../../../../shared/enum/currency-type";
import { UnitType } from '../../../../shared/enum/unit-type';
import {AssetBase} from "../../models/asset-base.model";
import {LoadingSpinnerComponent} from "../../../../shared/ui/loading/loading-spinner.component";
import {WalletApi} from "../../data-access/wallet.api";
import {CurrencyService} from "../../../../core/services/currency.service";

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
    private readonly assetService = inject(WalletApi);
    private readonly currencyService = inject(CurrencyService);
    private readonly translate = inject(TranslateService);

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
                this.filteredAssets = assets.slice(0, 100);
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
            this.filteredAssets = this._assets.slice(0, 100);
            return;
        }

        this.filteredAssets = this._assets
            .filter(a =>
                (a.name && a.name.toLowerCase().includes(t)) ||
                (a.symbol && a.symbol.toLowerCase().includes(t))
            )
            .slice(0, 100);
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
