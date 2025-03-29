import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";
import {PreferenceService} from "./preference-service";
import {CurrencyType} from "../model/currency-type";

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

    setGlobalCurrencyType(currencyType: CurrencyType): void {
        try {
            this.preferenceService.setPreferredCurrency(currencyType);
            this.setSelectedCurrencyType(currencyType);
        } catch (error) {
            console.error('Failed to set global currency type', error);
        }
    }

    getGlobalCurrencyType(): CurrencyType {
        try {
            return this.preferenceService.getPreferredCurrency();
        } catch (error) {
            console.error('Failed to get global currency type', error);
            return CurrencyType.PLN;
        }
    }
}