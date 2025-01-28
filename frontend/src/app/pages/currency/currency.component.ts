import { Component } from '@angular/core';
import {NgForOf} from '@angular/common';
import {Currency} from '../wallet/currency';
import {PreferenceService} from '../../services/preference-service';

@Component({
  selector: 'app-currency',
  standalone: true,
  imports: [
    NgForOf,
  ],
  templateUrl: './currency.component.html',
  styleUrl: './currency.component.css'
})
export class CurrencyComponent {
  currencyOptions = Object.values(Currency);

  constructor(private preference: PreferenceService) {
  }

  changeCurrency(event: Event) {
    const selectedCurrency = (event.target as HTMLInputElement).value;
    this.setPreferredCurrency(selectedCurrency);
  }

  private setPreferredCurrency(selectedCurrency: string) {
    this.preference.setPreferredCurrency(selectedCurrency);
  }
}
