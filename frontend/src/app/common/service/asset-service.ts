import {Injectable} from "@angular/core";
import {BehaviorSubject, Observable} from "rxjs";
import {AssetType} from "../enum/asset-type";
import {AssetDetail} from "../model/asset-detail.model";
import {CurrencyType} from "../enum/currency-type";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {API_ENDPOINTS} from "./api-endpoints";
import {AssetBase} from "../model/asset-base.model";

@Injectable({
    providedIn: 'root'
})
export class AssetService {
    private selectedAssetType = new BehaviorSubject<AssetType>(AssetType.CRYPTO);
    selectedAssetType$ = this.selectedAssetType.asObservable();

    private selectedAsset = new BehaviorSubject<AssetDetail | null>(null);
    selectedAsset$ = this.selectedAsset.asObservable();

    constructor(protected httpClient: HttpClient) {}

    setSelectedAssetType(assetType: AssetType) {
        this.selectedAssetType.next(assetType);
    }

    setSelectedAsset(asset: AssetDetail | null) {
        this.selectedAsset.next(asset);
    }

    getSelectedAssetType() {
        return this.selectedAssetType.getValue();
    }

    getSelectedAsset() {
        return this.selectedAsset.getValue();
    }

    getAssetByAssetTypeAndUri(assetType: AssetType, uri: string): Observable<AssetDetail> {
        return this.httpClient.get<AssetDetail>(`${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${assetType}`)
    }

    getAssetByAssetTypeAndUriAndCurrency(assetType: AssetType, uri: string, currencyType: CurrencyType): Observable<AssetDetail> {
        return this.httpClient.get<AssetDetail>(`${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${assetType}/${uri}/${currencyType}`)
    }

    getAssetsByAssetType(assetType: AssetType): Observable<AssetDetail[]> {
        return this.httpClient.get<AssetDetail[]>(`${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${assetType}`)
    }

    getAssetsBaseByAssetType(assetType: AssetType): Observable<AssetBase[]> {
        return this.httpClient.get<AssetBase[]>(`${environment.baseUrl}${API_ENDPOINTS.FINANCE}/base/${assetType}`)
    }

    getAssetsByAssetTypeAndCurrencyType(assetType: AssetType, currencyType: CurrencyType): Observable<AssetDetail[]> {
        return this.httpClient.get<AssetDetail[]>(`${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${assetType}/currencies/${currencyType}`)
    }
}