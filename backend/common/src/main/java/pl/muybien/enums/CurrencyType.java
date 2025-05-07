package pl.muybien.enums;

public enum CurrencyType {
    USD("$"),
    PLN("zł"),
    GBP("£"),
    EUR("€"),
    CNY("¥"),
    MYR("RM"),
    CAD("C$"),
    AUD("A$"),
    INR("₹"),
    BRL("R$"),
    NOK("kr");

    private final String symbol;

    CurrencyType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}
