package pl.muybien.finance.currency;

import pl.muybien.enums.CurrencyType;

import java.math.BigDecimal;

public interface CurrencyService {
    BigDecimal getCurrencyPairExchange(CurrencyType from, CurrencyType to);
}
