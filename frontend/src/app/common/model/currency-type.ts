export enum CurrencyType {
  USD = 'USD',
  PLN = 'PLN',
  EUR = 'EUR',
  GBP = 'GBP',
  CNY = 'CNY',
  MYR = 'MYR',
  CAD = 'CAD',
  AUD = 'AUD',
  INR = 'INR',
  BRL = 'BRL',
  NOK = 'NOK',
}

export namespace CurrencyType {
  export const Symbols: Record<CurrencyType, string> = {
    [CurrencyType.USD]: '$',
    [CurrencyType.PLN]: 'zł',
    [CurrencyType.EUR]: '€',
    [CurrencyType.GBP]: '£',
    [CurrencyType.CNY]: '¥',
    [CurrencyType.MYR]: 'RM',
    [CurrencyType.CAD]: 'C$',
    [CurrencyType.AUD]: 'A$',
    [CurrencyType.INR]: '₹',
    [CurrencyType.BRL]: 'R$',
    [CurrencyType.NOK]: 'kr',
  };
}
