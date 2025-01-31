import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Currency} from "../../models/currency";
import {PreferenceService} from "../../services/preference-service";
import {NgForOf} from "@angular/common";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-currency',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule
  ],
  templateUrl: './currency.component.html',
  styleUrl: './currency.component.css'
})

export class CurrencyComponent implements OnInit {
  currencyOptions: string[] = [];
  selectedCurrency!: string;

  @Output() currencyChange = new EventEmitter<string>();

  constructor(private preference: PreferenceService) {}

  ngOnInit() {
    this.currencyOptions = Object.values(Currency) as string[];
    this.selectedCurrency = this.preference.getPreferredCurrency() || this.currencyOptions[0];
  }

  onCurrencyChange(newCurrency: string) {
    this.currencyChange.emit(newCurrency);
    this.preference.setPreferredCurrency(newCurrency);
  }
}