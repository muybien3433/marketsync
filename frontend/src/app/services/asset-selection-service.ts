import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class AssetSelectionService {
    private selectedAssetType = new BehaviorSubject<string>('');
    selectedAssetType$ = this.selectedAssetType.asObservable();

    private selectedAssetUri = new BehaviorSubject<string>('');
    selectedAssetUri$ = this.selectedAssetUri.asObservable();

    setSelectedAssetType(type: string) {
        this.selectedAssetType.next(type);
    }

    setSelectedAssetUri(uri: string) {
        this.selectedAssetUri.next(uri);
    }

    getSelectedAssetType() {
        return this.selectedAssetType.getValue();
    }

    getSelectedAssetUri() {
        return this.selectedAssetUri.getValue();
    }
}