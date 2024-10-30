import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {CurrencyPipe, DecimalPipe, NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClient, HttpClientModule} from '@angular/common/http';

interface Asset {
  name: string;
  value: number;
  count: number;
  averagePurchasePrice: number;
  currentPrice: number;
  investmentPeriodInDays: number;
  profitInPercentage: number;
  profit: number;
}


@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [
    HttpClientModule,
    RouterOutlet,
    NgIf,
    FormsModule,
    CurrencyPipe,
    DecimalPipe,
    NgForOf
  ],
  templateUrl: './wallet.component.html',
  styleUrl: './wallet.component.css'
})

export class WalletComponent implements OnInit {
  assets: Asset[] = []; // Array to hold user assets

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchUserAssets();
  }

  // Fetch user's assets from the backend
  fetchUserAssets() {
    this.http.get<Asset[]>('http://localhost:8080/api/v1/wallet')
      .subscribe(
        (data) => {
          this.assets = data;
          this.calculateProfits(); // Calculate profits after fetching assets
        },
        (error) => {
          console.error('Error fetching user assets:', error);
        }
      );
  }

  // Calculate profits for each asset
  calculateProfits() {
    this.assets.forEach(asset => {
      asset.profit = (asset.currentPrice - asset.averagePurchasePrice) * asset.count;
      asset.profitInPercentage = (asset.profit / (asset.averagePurchasePrice * asset.count)) * 100;
    });
  }
}
