import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {CardComponent} from "../../../../shared/ui/card/card.component";
import {FormsModule} from "@angular/forms";
import {
    CurrencyChangeOptionComponent
} from "../../../../shared/components/currency-change-option/currency-change-option.component";
import {TranslatePipe} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {CurrencyType} from "../../../../shared/enum/currency-type";
import {NgIf} from "@angular/common";
import {CurrencyService} from "../../../../core/services/currency.service";

@Component({
    selector: 'app-setting-list',
    imports: [
        CardComponent,
        FormsModule,
        CurrencyChangeOptionComponent,
        TranslatePipe,
        NgIf
    ],
    templateUrl: './setting-list.component.html',
    styleUrl: './setting-list.component.scss'
})
export default class SettingListComponent implements OnInit, OnDestroy {
    private readonly currencyService = inject(CurrencyService);

    showSuccessMessage: boolean = false;

    private currencyTypeSubscription!: Subscription;
    currencyType!: CurrencyType;

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
