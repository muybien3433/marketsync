import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {NgForOf, NgIf} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {TranslatePipe} from "@ngx-translate/core";
import {FilterByNamePipe} from "../../../services/filter-by-name-pipe";
import {HttpClient} from "@angular/common/http";
import {AssetService} from "../../../services/asset.service";
import {environment} from "../../../../environments/environment";
import {API_ENDPOINTS} from "../../../services/api-endpoints";
import {catchError, map, of} from "rxjs";
import {AssetType} from "../../../models/asset-type";
import {AssetDetail} from "../../../models/asset-detail";

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
    @Input() assets: AssetDetail[] = [];
    @Input() selectedCurrency: string = '';
    @Output() assetSelected = new EventEmitter<any>();
    @Output() assetReset = new EventEmitter<any>();
    @ViewChild('assetSelect') assetSelect!: MatSelect;

    assetTypeOptions = Object.values(AssetType)
    selectedAsset: AssetDetail | undefined;
    addAssetForm: FormGroup;

    constructor(
        private http: HttpClient,
        private fb: FormBuilder,
        private assetService: AssetService,
    ) {
        this.addAssetForm = this.fb.group({
            assetType: [AssetType.CRYPTO, Validators.required],
            searchTerm: ['']
        })
    }

    ngOnInit(): void {
        this.onTypeSelect(AssetType.CRYPTO, this.selectedCurrency);
    }

    onTypeSelect(assetType: string, currencyType: string): void {
        this.assetService.setSelectedAssetType(assetType);
        this.fetchAssets(assetType, currencyType);
    }

    onAssetSelect(uri: string) {
        const selectedAsset = this.assets.find(asset => asset.uri === uri);
        if (selectedAsset) {
            this.selectedAsset = selectedAsset;
            this.assetService.setSelectedAssetUri(uri);
            this.assetSelected.emit(selectedAsset);
        }
    }

    getListSize() {
        return this.assets.length;
    }

    resetPickedAsset() {
        this.selectedAsset = undefined;
        this.assetReset.emit();
    }

    fetchAssets(assetType: string, currencyType: string) {
        this.http
            .get<any[]>(`${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${assetType}/currencies/${currencyType}`)
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
