import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";
import {PreferenceService} from "./preference-service";

@Injectable({
    providedIn: 'root'
})
export class CurrencyService {
    private selectedCurrencyType = new BehaviorSubject<string>('');
    selectedCurrencyType$ = this.selectedCurrencyType.asObservable();

    constructor(private preferenceService: PreferenceService) {
        this.setSelectedCurrencyType(this.preferenceService.getPreferredCurrency())
    }

    setSelectedCurrencyType(currencyType: string): void {
        this.selectedCurrencyType.next(currencyType);
    }

    getSelectedCurrencyType() {
        return this.selectedCurrencyType.getValue();
    }
}