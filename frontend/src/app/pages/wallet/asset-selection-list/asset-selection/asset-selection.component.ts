import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {MatSelectModule} from '@angular/material/select';
import {NgForOf, TitleCasePipe} from '@angular/common';
import {AssetListComponent} from '../asset-list/asset-list/asset-list.component';
import {TranslatePipe} from '@ngx-translate/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {environment} from '../../../../../environments/environment.development';
import {API_ENDPOINTS} from '../../../../services/api-endpoints';
import {catchError, map, of} from 'rxjs';

@Component({
  selector: 'app-asset-selection',
  standalone: true,
  imports: [
    MatSelectModule,
    NgForOf,
    TitleCasePipe,
    AssetListComponent,
    TranslatePipe,
    ReactiveFormsModule
  ],
  templateUrl: './asset-selection.component.html',
  styleUrl: './asset-selection.component.css'
})
export class AssetSelectionComponent {
  assetTypes = ['cryptos', 'stocks']
  assets: any[] = [];

  @Output() assetSelected = new EventEmitter<string>();
  addAssetForm: FormGroup;

  constructor(private http: HttpClient, private fb: FormBuilder) {
    this.addAssetForm = this.fb.group({
      type: ['', Validators.required],
      count: [0, [Validators.required, Validators.min(0.01)]],
      purchasePrice: [0, [Validators.required, Validators.min(0.01)]],
    });
  }

  fetchAssets(type: string) {
    console.log(`${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${type}`);
    this.http
      .get<any[]>(`${environment.baseUrl}${API_ENDPOINTS.FINANCE}/${type}`)
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

  onTypeChange(type: string) {
    this.fetchAssets(type);
  }
}
