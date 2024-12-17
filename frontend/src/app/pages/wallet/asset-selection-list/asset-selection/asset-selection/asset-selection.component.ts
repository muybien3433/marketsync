import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {MatSelectModule} from '@angular/material/select';
import {NgForOf, TitleCasePipe} from '@angular/common';
import {AssetListComponent} from '../../asset-list/asset-list/asset-list.component';
import {TranslatePipe} from '@ngx-translate/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

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
export class AssetSelectionComponent implements OnInit {
  baseUri = 'http://localhost:9999/api/v1'
  assetTypes = ['crypto', 'stock']
  selectedType = 'crypto'; // default
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

  ngOnInit() {
    this.fetchAssets(this.selectedType);
  }

  fetchAssets(type: string) {
    const url = this.baseUri + `/finances/${type}`;
    this.http.get<any[]>(url).subscribe((data) => {
        this.assets = Object.values(data);
      },
      (error) => {
        console.error('Error fetching assets: ', error);
        this.assets = [];
      }
    )
  }

  onTypeChange(type: string) {
    this.selectedType = type;
    this.fetchAssets(type);
  }
}
