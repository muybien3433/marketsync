import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";
import {AssetType} from "../model/asset-type";
import {AssetDetail} from "../model/asset-detail";

@Injectable({
    providedIn: 'root'
})
export class AssetService {
    private selectedAssetType = new BehaviorSubject<AssetType>(AssetType.CRYPTO);
    selectedAssetType$ = this.selectedAssetType.asObservable();

    private selectedAsset = new BehaviorSubject<AssetDetail | null>(null);
    selectedAsset$ = this.selectedAsset.asObservable();

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
}