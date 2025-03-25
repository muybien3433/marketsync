import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CurrencyType} from "../../model/currency-type";
import {CurrencyService} from "../../service/currency-service";

@Component({
  selector: 'app-currency-change-option',
  standalone: true,
  imports: [
    NgForOf,
    ReactiveFormsModule,
    FormsModule,
  ],
  templateUrl: './currency-change-option.component.html',
  styleUrl: './currency-change-option.component.scss'
})
export class CurrencyChangeOptionComponent implements OnInit {
  @Output() currencyChanged = new EventEmitter<CurrencyType>();

  currencyOptions = Object.values(CurrencyType);
  selectedCurrency!: CurrencyType;

  constructor(private currencyService: CurrencyService) {}

  ngOnInit(): void {
      this.selectedCurrency = this.currencyService.getGlobalCurrencyType();
  }

  onCurrencyChange(currency: CurrencyType): void {
    this.onCurrencySelect(currency);
  }

  onCurrencySelect(selectedCurrencyType: CurrencyType) {
    this.currencyService.setSelectedCurrencyType(selectedCurrencyType);
    this.selectedCurrency = selectedCurrencyType;
    this.currencyChanged.emit(selectedCurrencyType);
  }
}
