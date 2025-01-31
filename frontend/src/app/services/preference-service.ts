import {Injectable} from "@angular/core";

@Injectable({
    providedIn: 'root',
})
export class PreferenceService {
    private storageKey = 'preferredCurrency';

    setPreferredCurrency(currency: string): void {
        localStorage.setItem(this.storageKey, currency);
    }

    getPreferredCurrency(): string | null {
        return localStorage.getItem(this.storageKey);
    }
}