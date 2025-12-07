import {Component, OnInit} from '@angular/core';
import {TranslatePipe} from "@ngx-translate/core";
import {HttpClient} from '@angular/common/http';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {Router} from '@angular/router';
import {environment} from '../../../../environments/environment';
import {AssetHistory} from "../../../common/model/asset-history.model";
import {API_ENDPOINTS} from "../../../common/service/api-endpoints";
import {CardComponent} from "../../../common/components/card/card.component";
import {FormsModule} from "@angular/forms";
import { CurrencyType } from 'src/app/common/enum/currency-type';

@Component({
  selector: 'app-wallet-asset-history',
  standalone: true,
    imports: [
        TranslatePipe,
        NgForOf,
        DatePipe,
        NgIf,
        CardComponent,
        FormsModule,
    ],
  templateUrl: './wallet-asset-history.component.html',
  styleUrl: './wallet-asset-history.component.scss'
})
export default class WalletAssetHistoryComponent implements OnInit {
  protected _assets: AssetHistory[] = [];
  isLoading: boolean = true;
  showSuccessMessage: boolean = false;
  CurrencyType = CurrencyType;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.showSuccessMessage = false;
      this.fetchWalletAssetsHistory();
    }, 200)
  }

  editAsset(asset: AssetHistory) {
    this.router.navigate(['wallet/asset/edit'], { state: { asset } });
  }

  addAssetButton() {
    this.router.navigate(['wallet/asset/add']);
  }

  fetchWalletAssetsHistory() {
    this.http.get<AssetHistory[]>(`${environment.baseUrl}${API_ENDPOINTS.WALLET_HISTORY}`).subscribe({
      next: (assets) => {
        this._assets = Array.isArray(assets) ? assets : [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this._assets = [];
        this.isLoading = false;
      },
    });
  }

  deleteAsset(assetId: number) {
    this.http.delete(`${environment.baseUrl}${API_ENDPOINTS.WALLET}/${assetId}`).subscribe({
      next: () => {
        this._assets = this._assets.filter(asset => asset.id !== assetId);
        this.showSuccessMessage = true;
      },
      error: (err) => {
        console.error(err);
      }
    })
  }
}
