import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {CurrencyPipe, DecimalPipe, NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClient, HttpClientModule, HttpErrorResponse} from '@angular/common/http';

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
  assets: Asset[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchUserAssets();
  }

  fetchUserAssets() {
    this.http.get<Asset[]>('http://localhost:8080/api/v1/wallet', { withCredentials: true })
      .subscribe(
        (data) => {
          this.assets = data;
          // this.calculateProfits();
        },
        (error: HttpErrorResponse) => {
          console.error('Error fetching user assets:', error);
          console.log('Response body:', error.error); // Log the actual error response
          alert('Failed to load assets. Please try again later.');
        }
      );
  }

  // calculateProfits() {
  //   this.assets.forEach(asset => {
  //     const totalInvestment = asset.averagePurchasePrice * asset.count;
  //     asset.profit = (asset.currentPrice - asset.averagePurchasePrice) * asset.count;
  //
  //     asset.profitInPercentage = totalInvestment > 0
  //       ? (asset.profit / totalInvestment) * 100
  //       : 0; // Set to 0 if no investment
  //   });
  // }

}
