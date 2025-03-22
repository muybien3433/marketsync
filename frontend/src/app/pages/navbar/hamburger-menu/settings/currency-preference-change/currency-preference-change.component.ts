import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {CurrencyType} from "../../../../../models/currency-type";
import {PreferenceService} from "../../../../../services/preference-service";
import {NgForOf} from "@angular/common";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-currency-preference-change',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule
  ],
  templateUrl: './currency-preference-change.component.html',
  styleUrl: './currency-preference-change.component.css'
})

export class CurrencyPreferenceChangeComponent implements OnInit {
  currencyOptions: string[] = [];
  selectedCurrency!: string;

  @Output() currencyChange = new EventEmitter<string>();

  constructor(private preference: PreferenceService) {}

  ngOnInit() {
    this.currencyOptions = Object.values(CurrencyType) as string[];
    this.selectedCurrency = this.preference.getPreferredCurrency() || this.currencyOptions[0];
  }

  onCurrencyChange(newCurrency: CurrencyType) {
    this.currencyChange.emit(newCurrency);
    this.preference.setPreferredCurrency(newCurrency);
  }
}