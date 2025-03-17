import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {NgForOf, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {TranslatePipe} from "@ngx-translate/core";
import {FilterByNamePipe} from "../../services/filter-by-name-pipe";
import {HttpClient} from "@angular/common/http";
import {AssetService} from "../../services/asset.service";
import {environment} from "../../../environments/environment";
import {API_ENDPOINTS} from "../../services/api-endpoints";
import {catchError, map, of} from "rxjs";
import {AssetType} from "../../models/asset-type";

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
    ],
    templateUrl: './asset-selection-list.component.html',
    styleUrl: './asset-selection-list.component.css'
})
export class AssetSelectionListComponent implements OnInit {
    @Input() assets: any[] = [];
    assetTypeOptions = Object.values(AssetType)
    selectedAsset: any = null;
    addAssetForm: FormGroup;

    @ViewChild('assetSelect') assetSelect!: MatSelect;

    constructor(
        private http: HttpClient,
        private fb: FormBuilder,
        private assetSelection: AssetService,
    ) {
        this.addAssetForm = this.fb.group({
            assetType: [AssetType.CRYPTO, Validators.required],
            searchTerm: ['']
        })
    }

    ngOnInit(): void {
        this.onTypeSelect(AssetType.CRYPTO);
    }

    onTypeSelect(assetType: string) {
        this.fetchAssets(assetType);
        this.selectedAsset = null;
        this.assetSelection.setSelectedAssetType(assetType);
    }

    onAssetSelect(uri: string) {
        this.selectedAsset = uri;
        this.assetSelection.setSelectedAssetUri(uri);
    }

    getListSize() {
        return this.assets.length;
    }

    resetPickedAsset() {
        this.selectedAsset = '';
    }

    fetchAssets(assetType: string) {
        this.http
            .get<any[]>(`${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${assetType}`)
            .pipe(
                map((data) => {
                    this.assets = Object.values(data);
                }),
                catchError((error) => {
                    console.error('Error fetching assets: ', error);
                    this.assets = [];
                    return of([]);
                })
            )
            .subscribe();
    }
}
