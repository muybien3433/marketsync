import {Component, Input} from '@angular/core';
import {AssetDetail} from "../../models/asset-detail";
import {CurrencyPipe, NgIf} from "@angular/common";
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-asset-price-display',
  standalone: true,
  imports: [
    CurrencyPipe,
    TranslatePipe,
    NgIf
  ],
  templateUrl: './asset-price-display.component.html',
  styleUrl: './asset-price-display.component.css'
})
export class AssetPriceDisplayComponent {
  @Input() selectedAsset: AssetDetail | undefined;
}
