import {Component, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {CurrencyPipe, DecimalPipe, NgClass, NgForOf, NgIf} from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule, HttpErrorResponse } from '@angular/common/http';
import { forkJoin, catchError, map, of } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

interface Asset {
  name: string;
  value: number;
  count: number;
  averagePurchasePrice: number;
  investmentPeriodInDays: number;
  currentPrice?: number;
  profit?: number;
  profitInPercentage?: number;
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
    NgForOf,
    NgClass
  ],
  templateUrl: './wallet.component.html',
  styleUrls: ['./wallet.component.css']
})
export class WalletComponent implements OnInit {
  assets: Asset[] = [];

  constructor(private http: HttpClient, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    this.fetchUserAssets();
  }

  fetchUserAssets(): void {
    this.http.get<Asset[]>('http://localhost:8080/api/v1/wallet', { withCredentials: true })
      .pipe(
        map(data => data.map(asset => ({
          ...asset,
          currentPrice: asset.currentPrice ?? 0,
          profit: asset.profit ?? 0,
          profitInPercentage: asset.profitInPercentage ?? 0
        }))),
        catchError((error: HttpErrorResponse) => {
          console.error('Error fetching user wallet:', error);
          this.snackBar.open('Failed to fetch assets. Please try again later.', '', {
            duration: 3000,
          });
          return of([]);
        })
      )
      .subscribe((assets) => {
        this.assets = assets;
        this.fetchCurrentPrices();
      });
  }

  fetchCurrentPrices(): void {
    const priceRequests = this.assets.map(asset =>
      this.http.get<number>(`http://localhost:8080/api/v1/finance/${asset.name}`, { withCredentials: true })
    );
    forkJoin(priceRequests).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Error fetching current prices:', error);
        this.snackBar.open('Failed to load current prices. Please try again later.', 'Close', {
          duration: 3000,
        });
        return of([]);
      })
    )
      .subscribe(prices => {
        prices.forEach((price, index) => {
          if (price !== undefined) {
            this.assets[index].currentPrice = price;
            this.assets[index].profit = this.calculateProfit(this.assets[index]);
            this.assets[index].profitInPercentage = this.calculateProfitPercentage(this.assets[index]);
          }
        });
      });
  }

  calculateProfit(asset: Asset): number {
    const averagePurchasePrice = asset.averagePurchasePrice;
    return ((asset.currentPrice ?? 0) - averagePurchasePrice) * asset.count;
  }

  calculateProfitPercentage(asset: Asset): number {
    const averagePurchasePrice = asset.averagePurchasePrice;
    if (averagePurchasePrice === 0) {
      return 0;
    }
    const profitPercentage = ((asset.currentPrice ?? 0 - averagePurchasePrice) / averagePurchasePrice) * 100;

    return parseFloat((profitPercentage - 100).toFixed(2));
  }

}
