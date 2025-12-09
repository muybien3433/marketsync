import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable, combineLatest, EMPTY } from "rxjs";
import { AssetType } from "../enum/asset-type";
import { AssetDetail } from "../model/asset-detail.model";
import { CurrencyType } from "../enum/currency-type";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { API_ENDPOINTS } from "./api-endpoints";
import { AssetBase } from "../model/asset-base.model";
import { CurrencyService } from "./currency-service";
import { catchError, filter, switchMap, tap } from "rxjs/operators";
import { UnitType } from "../enum/unit-type";

@Injectable({
    providedIn: "root"
})
export class AssetService {
    private selectedAssetType = new BehaviorSubject<AssetType>(AssetType.CRYPTO);
    selectedAssetType$ = this.selectedAssetType.asObservable();

    private selectedAssetRef = new BehaviorSubject<{ type: AssetType; uri: string } | null>(null);

    private selectedAsset = new BehaviorSubject<AssetDetail | null>(null);
    selectedAsset$ = this.selectedAsset.asObservable();

    constructor(
        protected httpClient: HttpClient,
        private currencyService: CurrencyService
    ) {
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
            `${environment.baseUrl}${API_ENDPOINTS.FINANCE}/base/${assetType}`
        );
    }

    getAssetsByAssetType(assetType: AssetType): Observable<AssetDetail[]> {
        return this.httpClient.get<AssetDetail[]>(
            `${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${assetType}`
        );
    }

    getAssetsByAssetTypeAndCurrencyType(
        assetType: AssetType,
        currencyType: CurrencyType
    ): Observable<AssetDetail[]> {
        return this.httpClient.get<AssetDetail[]>(
            `${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${assetType}/currencies/${currencyType}`
        );
    }

    getAssetByAssetTypeAndUriAndCurrency(
        assetType: AssetType,
        uri: string,
        currencyType: CurrencyType
    ): Observable<AssetDetail> {
        return this.httpClient.get<AssetDetail>(
            `${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${assetType}/${uri}/${currencyType}`
        );
    }

    private normalizeUnitType(v: any): UnitType {
        if (v === undefined || v === null) return UnitType.UNIT;
        if (typeof v === "string" && v.trim() === "") return UnitType.UNIT;
        return v as UnitType;
    }
}
