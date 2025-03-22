import {Injectable} from "@angular/core";
import {CurrencyType} from "../models/currency-type";

@Injectable({
    providedIn: 'root',
})
export class PreferenceService {
    private storageKey = 'preferredCurrency';

    setPreferredCurrency(currencyType: CurrencyType): void {
        localStorage.setItem(this.storageKey, currencyType);
    }

    getPreferredCurrency(): CurrencyType  {
        const storedCurrency = localStorage.getItem(this.storageKey);

        if (storedCurrency && Object.values(CurrencyType).includes(storedCurrency as CurrencyType)) {
            return storedCurrency as CurrencyType;
        }
        return CurrencyType.PLN
    }
}