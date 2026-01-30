import {inject, Injectable} from "@angular/core";
import { BehaviorSubject, Observable, combineLatest, EMPTY } from "rxjs";
import { AssetType } from "../../../shared/enum/asset-type";
import { AssetDetail } from "../models/asset-detail.model";
import { CurrencyType } from "../../../shared/enum/currency-type";
import { HttpClient } from "@angular/common/http";
import { API_ENDPOINTS } from "../../../core/http/api/api-endpoints";
import { AssetBase } from "../models/asset-base.model";
import { catchError, filter, switchMap, tap } from "rxjs/operators";
import { UnitType } from "../../../shared/enum/unit-type";
import {CurrencyService} from "../../../core/services/currency.service";

@Injectable({
    providedIn: "root"
})
export class WalletApi {
    private readonly httpClient = inject(HttpClient);
    private readonly currencyService = inject(CurrencyService);

    private selectedAssetType = new BehaviorSubject<AssetType>(AssetType.CRYPTO);
    selectedAssetType$ = this.selectedAssetType.asObservable();

    private selectedAssetRef = new BehaviorSubject<{ type: AssetType; uri: string } | null>(null);

    private selectedAsset = new BehaviorSubject<AssetDetail | null>(null);
    selectedAsset$ = this.selectedAsset.asObservable();

    constructor() {
        combineLatest([
            this.selectedAssetRef,
            this.currencyService.selectedCurrencyType$
        ])
            .pipe(
                filter(([ref, currency]) => !!ref),
                switchMap(([ref, currency]) =>
                    this.getAssetByAssetTypeAndUriAndCurrency(ref!.type, ref!.uri, currency).pipe(
                        tap(detail => {
                            const normalized: AssetDetail = {
                                ...detail,
                                unitType: this.normalizeUnitType((detail as any).unitType)
                            };
                            this.selectedAsset.next(normalized);
                        }),
                        catchError(err => {
                            console.error("Error fetching asset detail for", ref, err);
                            this.selectedAsset.next(null);
                            return EMPTY;
                        })
                    )
                )
            )
            .subscribe();
    }

    setSelectedAssetType(assetType: AssetType) {
        this.selectedAssetType.next(assetType);
    }

    setSelectedAssetRef(assetType: AssetType, uri: string) {
        this.selectedAssetRef.next({ type: assetType, uri });
    }

    setSelectedAsset(asset: AssetDetail | null) {
        this.selectedAsset.next(asset);
        this.selectedAssetRef.next(null);
    }

    getSelectedAssetType() {
        return this.selectedAssetType.getValue();
    }

    getSelectedAsset() {
        return this.selectedAsset.getValue();
    }

    getAssetsBaseByAssetType(assetType: AssetType): Observable<AssetBase[]> {
        return this.httpClient.get<AssetBase[]>(
            `${API_ENDPOINTS.FINANCE}/base/${assetType}`
        );
    }

    getAssetsByAssetType(assetType: AssetType): Observable<AssetDetail[]> {
        return this.httpClient.get<AssetDetail[]>(
            `${API_ENDPOINTS.FINANCE}/${assetType}`
        );
    }

    getAssetsByAssetTypeAndCurrencyType(
        assetType: AssetType,
        currencyType: CurrencyType
    ): Observable<AssetDetail[]> {
        return this.httpClient.get<AssetDetail[]>(
            `${API_ENDPOINTS.FINANCE}/${assetType}/currencies/${currencyType}`
        );
    }

    getAssetByAssetTypeAndUriAndCurrency(
        assetType: AssetType,
        uri: string,
        currencyType: CurrencyType
    ): Observable<AssetDetail> {
        return this.httpClient.get<AssetDetail>(
            `${API_ENDPOINTS.FINANCE}/${assetType}/${uri}/${currencyType}`
        );
    }

    private normalizeUnitType(v: any): UnitType {
        if (v === undefined || v === null) return UnitType.UNIT;
        if (typeof v === "string" && v.trim() === "") return UnitType.UNIT;
        return v as UnitType;
    }
}
