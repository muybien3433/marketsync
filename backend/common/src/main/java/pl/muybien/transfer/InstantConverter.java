package pl.muybien.transfer;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InstantConverter implements ImportConverter<Instant> {

    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw"); // adjust if needed

    @Override
    public Instant convert(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        if (s.matches("^\\d{13}$")) return Instant.ofEpochMilli(Long.parseLong(s));
        if (s.matches("^\\d{10}$")) return Instant.ofEpochSecond(Long.parseLong(s));

        try { return Instant.parse(s); } catch (DateTimeParseException ignored) {}
        try { return OffsetDateTime.parse(s).toInstant(); } catch (DateTimeParseException ignored) {}

        try { return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(ZONE).toInstant(); }
        catch (DateTimeParseException ignored) {}

        try { return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(ZONE).toInstant(); }
        catch (DateTimeParseException ignored) {}

        throw new IllegalArgumentException("Unparseable instant: " + s);
    }
}
