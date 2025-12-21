package pl.muybien.transfer;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface ImportConverter<T> {
    T convert(String raw) throws Exception;

    final class Auto implements ImportConverter<Object> {
        @Override public Object convert(String raw) { return raw; }

        public static Object convertByType(String raw, Class<?> target) {
            if (raw == null || raw.isBlank()) return null;

            String s = raw.trim();
            if (target == String.class) return s;

            if (target == Boolean.class || target == boolean.class) {
                String t = s.toLowerCase();
                return t.equals("true") || t.equals("tak") || t.equalsIgnoreCase("Tak") || t.equalsIgnoreCase("yes");
            }

            String v = normalizeNumeric(s);

            if (target == Long.class || target == long.class) {
                BigDecimal bd = new BigDecimal(v);
                try { return bd.longValueExact(); } catch (ArithmeticException ex) { throw new IllegalArgumentException("Not an integer: " + s); }
            }
            if (target == Integer.class || target == int.class) {
                BigDecimal bd = new BigDecimal(v);
                try { return bd.intValueExact(); } catch (ArithmeticException ex) { throw new IllegalArgumentException("Not an integer: " + s); }
            }
            if (target == BigInteger.class) {
                BigDecimal bd = new BigDecimal(v);
                try { return bd.toBigIntegerExact(); } catch (ArithmeticException ex) { throw new IllegalArgumentException("Not an integer: " + s); }
            }
            if (target == BigDecimal.class) return new BigDecimal(v);
            if (target == Double.class || target == double.class) return new BigDecimal(v).doubleValue();
            if (target == Float.class  || target == float.class)  return new BigDecimal(v).floatValue();

            throw new IllegalArgumentException("No default converter for type " + target.getName());
        }

        private static String normalizeNumeric(String s) {
            String v = s;
            v = v.replace("\u00A0", "").replace("\u2007", "").replace("\u202F", "").replace(" ", "");
            if (v.indexOf(',') >= 0 && v.indexOf('.') < 0) v = v.replace(',', '.'); else v = v.replace(",", "");
            v = v.replaceAll("[eE][+-]?$", "");
            return v;
        }
    }
}
