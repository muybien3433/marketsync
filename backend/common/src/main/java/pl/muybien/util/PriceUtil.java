package pl.muybien.util;

import pl.muybien.entity.helper.FinanceDetail;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtil {

    private static final int MAX_DECIMALS = 18;

    public static BigDecimal normalizePrice(BigDecimal value) {
        if (value == null) return null;

        // If the value is greater than or equal to 1, keep only two decimal places.
        if (value.compareTo(BigDecimal.ONE) >= 0) {
            return value.setScale(2, RoundingMode.HALF_UP);
        }

        // For values lower than 1, find the first non-zero digit after the decimal point.
        String plain = value.stripTrailingZeros().toPlainString();
        int idx = plain.indexOf('.');
        if (idx < 0) {
            // No decimal point found — treat as integer and keep two decimals.
            return value.setScale(2, RoundingMode.HALF_UP);
        }

        int firstNonZero = -1;
        for (int i = idx + 1; i < plain.length(); i++) {
            if (plain.charAt(i) != '0') {
                firstNonZero = i - idx;
                break;
            }
        }

        if (firstNonZero < 0) {
            // All zeros after the decimal point — fallback to minimal precision.
            return value.setScale(8, RoundingMode.HALF_UP);
        }

        // Keep four digits after the first non-zero one, but never exceed MAX_DECIMALS.
        int scale = Math.min(firstNonZero + 4, MAX_DECIMALS);
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

    // Builds a readable string combining the asset symbol and name.
    public String toNameWithSymbol(FinanceDetail financeDetail) {
        return "(" + financeDetail.symbol() + ") " + financeDetail.name();
    }
}
