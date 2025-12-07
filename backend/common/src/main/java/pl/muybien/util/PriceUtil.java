package pl.muybien.util;

import pl.muybien.entity.helper.FinanceDetail;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtil {

    public static BigDecimal normalizePrice(BigDecimal value) {
        if (value == null) return null;

        // if >= 1 -> 2 places after the decimal
        if (value.compareTo(BigDecimal.ONE) >= 0) {
            return value.setScale(2, RoundingMode.HALF_UP);
        }

        // lower than 1 – finding first not zero digit after the decimal
        String plain = value.stripTrailingZeros().toPlainString();
        int idx = plain.indexOf('.');
        if (idx < 0) {
            return value.setScale(2, RoundingMode.HALF_UP);
        }

        int firstNonZero = -1;
        for (int i = idx + 1; i < plain.length(); i++) {
            if (plain.charAt(i) != '0') {
                firstNonZero = i - idx;
                break;
            }
        }

        int scale = Math.min(firstNonZero + 2, 8); // ex. 0.000024 -> 6 places
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

    public String toNameWithSymbol(FinanceDetail financeDetail) {
        return "(" + financeDetail.symbol() + ") " + financeDetail.name() ;
    }
}
