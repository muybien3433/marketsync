export enum CurrencyType {
  USD = 'USD',
  PLN = 'PLN',
  EUR = 'EUR',
  GBP = 'GBP',
}

export namespace CurrencyType {
  export const Symbols: Record<CurrencyType, string> = {
    [CurrencyType.USD]: '$',
    [CurrencyType.PLN]: 'zł',
    [CurrencyType.EUR]: '€',
    [CurrencyType.GBP]: '£',
  };
}
