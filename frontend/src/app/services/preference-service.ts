import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class PreferenceService {
  private readonly storageKey = 'preferredCurrency';
  private defaultCurrency = 'USD';

  constructor() {
    if (!localStorage.getItem(this.storageKey)) {
      localStorage.setItem(this.storageKey, this.defaultCurrency);
    }
  }

  setPreferredCurrency(currency: string) {
    localStorage.setItem(this.storageKey, currency);
  }

  getPreferredCurrency(): string {
    return localStorage.getItem(this.storageKey) || this.defaultCurrency;
  }
}
