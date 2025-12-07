import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {NgForOf} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CurrencyType} from "../../enum/currency-type";
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
export class CurrencyChangeOptionComponent implements OnInit, OnDestroy {
  @Output() currencyChanged = new EventEmitter<CurrencyType>();

  currencyOptions = Object.values(CurrencyType).filter(value => typeof value === 'string') as CurrencyType[];
  selectedCurrency!: CurrencyType;
  CurrencyType = CurrencyType;

  constructor(private currencyService: CurrencyService) {}

  ngOnInit(): void {
    this.selectedCurrency = this.currencyService.getGlobalCurrencyType();
  }

  ngOnDestroy(): void {
    const currentGlobal = this.currencyService.getGlobalCurrencyType();
    if (this.currencyService.getSelectedCurrencyType() !== currentGlobal) {
      this.currencyService.setSelectedCurrencyType(currentGlobal);
    }
  }

  onCurrencyChange(currencyType: CurrencyType): void {
    this.onCurrencySelect(currencyType);
  }

  onCurrencySelect(selectedCurrencyType: CurrencyType) {
    this.currencyService.setSelectedCurrencyType(selectedCurrencyType);
    this.selectedCurrency = selectedCurrencyType;
    this.currencyChanged.emit(selectedCurrencyType);
  }
}
