import {Component, Input} from '@angular/core';
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatOption} from "@angular/material/core";
import {MatSelect} from "@angular/material/select";
import {NgForOf, NgIf, TitleCasePipe} from "@angular/common";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {TranslatePipe} from "@ngx-translate/core";
import {FilterByNamePipe} from "../../services/filter-by-name-pipe";
import {MatInput} from "@angular/material/input";
import {HttpClient} from "@angular/common/http";
import {AssetSelectionService} from "../../services/asset-selection-service";
import {environment} from "../../../environments/environment.development";
import {API_ENDPOINTS} from "../../services/api-endpoints";
import {catchError, map, of} from "rxjs";
import {AssetType} from "../../models/asset-type";
import {Router} from "@angular/router";

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
    TitleCasePipe,
    TranslatePipe,
    FilterByNamePipe,
    MatInput,
  ],
  templateUrl: './asset-selection-list.component.html',
  styleUrl: './asset-selection-list.component.css'
})
export class AssetSelectionListComponent {
  @Input() assets: any[] = [];
  assetTypeOptions = Object.values(AssetType)
  selectedAssetUri: string = '';
  addAssetForm: FormGroup;

  constructor(
      private http: HttpClient,
      private fb: FormBuilder,
      private assetSelection: AssetSelectionService
  ) {
    this.addAssetForm = this.fb.group({
      assetType: ['', Validators.required],
      searchTerm: ['']
    })
  }

  onTypeSelect(assetType: string) {
    this.fetchAssets(assetType);
    this.assetSelection.setSelectedAssetType(assetType);
  }

  onAssetSelect(uri: string) {
    this.assetSelection.setSelectedAssetUri(uri);
    this.selectedAssetUri = uri;
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
