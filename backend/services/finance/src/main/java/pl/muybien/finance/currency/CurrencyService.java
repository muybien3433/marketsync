package pl.muybien.finance.currency;

import java.math.BigDecimal;

public interface CurrencyService {
    BigDecimal getCurrencyPairValue(CurrencyType from, CurrencyType to);
}
