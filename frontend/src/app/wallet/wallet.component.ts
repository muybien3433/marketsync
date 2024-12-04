import { Component } from '@angular/core';
import { Asset } from './asset';
import {NgForOf, NgStyle} from '@angular/common';

@Component({
  selector: 'app-wallet',
  standalone: true,
  imports: [
    NgForOf,
    NgStyle
  ],
  templateUrl: './wallet.component.html',
  styleUrl: './wallet.component.css'
})
export class WalletComponent {

  assets: Asset[] = [
    new Asset("Bitcoin", 200000, 100000, 100000, 1, 0.0, 0),
    new Asset("Bitcoin", 100000, 50000, 100000, 1, 150.0, 50000)
  ];
}
