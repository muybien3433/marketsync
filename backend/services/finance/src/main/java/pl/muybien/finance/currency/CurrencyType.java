package pl.muybien.finance.currency;

public enum CurrencyType {
    USD,
    PLN,
    GBP,
    EUR;

    public static CurrencyType fromString(String currency) {
        if (currency != null) {
            try {
                return CurrencyType.valueOf(currency.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Currency type " + currency + " not supported");
            }
        }
        throw new IllegalArgumentException("Currency type is null");
    }
}
