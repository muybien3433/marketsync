import {Component, OnDestroy, OnInit} from '@angular/core';
import {CardComponent} from "../../common/components/card/card.component";
import {FormsModule} from "@angular/forms";
import {
  CurrencyChangeOptionComponent
} from "../../common/components/currency-change-option/currency-change-option.component";
import {TranslatePipe} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {CurrencyType} from "../../common/enum/currency-type";
import {CurrencyService} from "../../common/service/currency-service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-settings',
  imports: [
    CardComponent,
    FormsModule,
    CurrencyChangeOptionComponent,
    TranslatePipe,
    NgIf
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export default class SettingsComponent implements OnInit, OnDestroy {
  showSuccessMessage: boolean = false;

  private currencyTypeSubscription!: Subscription;
  currencyType!: CurrencyType;

  constructor(private currencyService: CurrencyService) {
  }

  ngOnInit(): void {
    this.currencyTypeSubscription = this.currencyService.selectedCurrencyType$.subscribe(currencyType => {
      this.showSuccessMessage = false;
      this.currencyType = currencyType;
    })
  }

  ngOnDestroy(): void {
    this.currencyTypeSubscription.unsubscribe();
  }

  saveCurrencySettings() {
    this.currencyService.setGlobalCurrencyType(this.currencyType);
    this.showSuccessMessage = true;
  }
}
