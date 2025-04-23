package pl.muybien.enums;

public enum CurrencyType {
    USD("$"),
    PLN("zł"),
    GBP("£"),
    EUR("€");

    private final String symbol;

    CurrencyType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}
