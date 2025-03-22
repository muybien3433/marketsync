import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";
import {PreferenceService} from "./preference-service";
import {CurrencyType} from "../models/currency-type";

@Injectable({
    providedIn: 'root'
})
export class CurrencyService {
    private selectedCurrencyType = new BehaviorSubject<CurrencyType>(CurrencyType.PLN);
    selectedCurrencyType$ = this.selectedCurrencyType.asObservable();

    constructor(private preferenceService: PreferenceService) {
        this.setSelectedCurrencyType(this.preferenceService.getPreferredCurrency())
    }

    setSelectedCurrencyType(currencyType: CurrencyType): void {
        this.selectedCurrencyType.next(currencyType);
    }

    getSelectedCurrencyType() {
        return this.selectedCurrencyType.getValue();
    }
}