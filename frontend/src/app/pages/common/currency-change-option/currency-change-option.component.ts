import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {NgForOf} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {CurrencyType} from "../../../models/currency-type";
import {CurrencyService} from "../../../services/currency-service";

@Component({
  selector: 'app-currency-change-option',
  standalone: true,
  imports: [
    NgForOf,
    ReactiveFormsModule,
  ],
  templateUrl: './currency-change-option.component.html',
  styleUrl: './currency-change-option.component.css'
})
export class CurrencyChangeOptionComponent implements OnInit {
  @Output() currencySelected: EventEmitter<any> = new EventEmitter();
  currencyOptions = Object.values(CurrencyType);

  constructor(private currencyService: CurrencyService) {}

  ngOnInit(): void {
    this.onCurrencySelect(this.currencyService.getSelectedCurrencyType());
  }

  onCurrencyChange(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;
    this.onCurrencySelect(selectedValue);
  }

  onCurrencySelect(selectedCurrencyType: string) {
    this.currencyService.setSelectedCurrencyType(selectedCurrencyType);
    this.currencySelected.emit(selectedCurrencyType);
  }
}
