package pl.muybien.finance.currency;

import java.math.BigDecimal;

public interface CurrencyService {
    BigDecimal getCurrencyPairExchange(CurrencyType from, CurrencyType to);
}
