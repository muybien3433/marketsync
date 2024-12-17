import {Component, Input} from '@angular/core';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import {NgForOf} from '@angular/common';
import {FilterByNamePipe} from '../../../../../services/filter-by-name-pipe';
import {TranslatePipe} from '@ngx-translate/core';
import {AssetService} from '../../../../../services/asset-service';

@Component({
  selector: 'app-asset-list',
  standalone: true,
  imports: [
    MatInputModule,
    FormsModule,
    NgForOf,
    FilterByNamePipe,
    TranslatePipe
  ],
  templateUrl: './asset-list.component.html',
  styleUrl: './asset-list.component.css'
})
export class AssetListComponent {
  @Input() assets: any[] = [];
  searchTerm: string = '';
  selectedAssetUri: string = '';

  constructor(private assetService: AssetService) {}

  selectAsset(uri: string): void {
    this.selectedAssetUri = uri;
    this.assetService.setAssetUri(uri);
  }
}
