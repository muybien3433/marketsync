package pl.muybien.service;

import lombok.RequiredArgsConstructor;
import pl.muybien.account.UserIdentityInput;
import pl.muybien.account.UserIdentityResolution;
import pl.muybien.account.UsernameAvailabilityChecker;
import pl.muybien.config.AccountSettingsProperties;
import pl.muybien.config.AccountSettingsProperties.LettersCase;
import pl.muybien.config.AccountSettingsProperties.PadWith;
import pl.muybien.config.AccountSettingsProperties.UniquenessStrategy;
import pl.muybien.config.AccountSettingsProperties.UsernameMode;
import pl.muybien.config.AccountSettingsProperties.UsernameScheme;
import pl.muybien.config.AccountSettingsProperties.EmailMode;
import pl.muybien.config.AccountSettingsProperties.EmailScheme;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AccountIdentityService {

    private final AccountSettingsProperties properties;
    private final UsernameAvailabilityChecker availabilityChecker;
    private final SecureRandom random = new SecureRandom();

    public UserIdentityResolution resolve(UserIdentityInput input) {
        String username = resolveUsername(input);
        String email = resolveEmail(input, username);
        boolean emailVerified = resolveEmailVerified(email);
        return new UserIdentityResolution(username, email, emailVerified);
    }

    private String resolveUsername(UserIdentityInput input) {
        UsernameMode mode = properties.getUsername().getMode();

        if (mode == UsernameMode.EMAIL) {
            String email = requireNonBlank(input.email(), "Email is required when username.mode=EMAIL");
            String u = emailLocalPart(email);
            u = normalizeUsername(u);
            return ensureUnique(u);
        }

        if (mode == UsernameMode.PROVIDED) {
            String u = requireNonBlank(input.username(), "Username is required when username.mode=PROVIDED");
            u = normalizeUsername(u);
            return ensureUnique(u);
        }

        String generated = generateUsername(input);
        generated = normalizeUsername(generated);
        return ensureUnique(generated);
    }

    private String generateUsername(UserIdentityInput input) {
        UsernameScheme scheme = properties.getUsername().getGeneration().getScheme();
        if (scheme == UsernameScheme.EMAIL_LOCALPART) {
            String email = requireNonBlank(input.email(), "Email is required for username.generation.scheme=EMAIL_LOCALPART");
            return emailLocalPart(email);
        }
        if (scheme == UsernameScheme.UUID) {
            return UUID.randomUUID().toString();
        }
        if (scheme == UsernameScheme.SLUG) {
            String base = joinNonBlank(" ", input.firstName(), input.lastName(), input.username(), input.email());
            if (isBlank(base)) base = UUID.randomUUID().toString();
            return slugify(base);
        }
        return generateNameBased(input);
    }

    private String generateNameBased(UserIdentityInput input) {
        AccountSettingsProperties.Username.Generation.NameBased nb = properties.getUsername().getGeneration().getNameBased();

        String first = safe(input.firstName());
        String last = safe(input.lastName());

        String firstPart = take(first, nb.getFirstName().getTake());
        String lastPart = take(last, nb.getLastName().getTake());

        int requiredFirst = nb.getFirstName().getTake();
        int requiredLast = nb.getLastName().getTake();

        if (firstPart.length() < requiredFirst || lastPart.length() < requiredLast) {
            int missing = Math.max(0, requiredFirst - firstPart.length()) + Math.max(0, requiredLast - lastPart.length());
            String pad = buildPad(nb.getWhenTooShort().getPadWith(), nb.getWhenTooShort().getPadLength(), missing);
            if (firstPart.length() < requiredFirst) firstPart = firstPart + padChunk(pad, requiredFirst - firstPart.length());
            if (lastPart.length() < requiredLast) lastPart = lastPart + padChunk(pad, requiredLast - lastPart.length());
        }

        String base = firstPart + safe(nb.getSeparator()) + lastPart;

        AccountSettingsProperties.Username.Generation.NameBased.RandomPart rp = nb.getRandom();
        StringBuilder suffix = new StringBuilder();
        if (rp.isAppendLetters() && rp.getLettersCount() > 0) {
            suffix.append(randomLetters(rp.getLettersCount(), rp.getLettersCase()));
        }
        if (rp.isAppendDigits() && rp.getDigitsCount() > 0) {
            suffix.append(randomDigits(rp.getDigitsCount()));
        }

        return base + suffix;
    }

    private String resolveEmail(UserIdentityInput input, String resolvedUsername) {
        EmailMode mode = properties.getEmail().getMode();
        if (mode == EmailMode.PROVIDED) {
            return requireNonBlank(input.email(), "Email is required when email.mode=PROVIDED");
        }

        EmailScheme scheme = properties.getEmail().getGeneration().getScheme();
        if (scheme == EmailScheme.DISABLED) {
            String email = input.email();
            if (isBlank(email)) throw new IllegalArgumentException("Email generation is disabled and email is missing");
            return email;
        }
        if (scheme == EmailScheme.FROM_USERNAME) {
            String u = requireNonBlank(resolvedUsername, "Resolved username is missing");
            return u + "@example.com";
        }

        String template = properties.getEmail().getGeneration().getFromTemplate().getTemplate();
        template = Objects.toString(template, "");
        String email = template
                .replace("{username}", Objects.toString(resolvedUsername, ""))
                .replace("{firstName}", Objects.toString(input.firstName(), ""))
                .replace("{lastName}", Objects.toString(input.lastName(), ""))
                .replace("{email}", Objects.toString(input.email(), ""));
        if (isBlank(email)) throw new IllegalArgumentException("Generated email is blank");
        return email;
    }

    private boolean resolveEmailVerified(String email) {
        if (isBlank(email)) return false;
        return !properties.getEmail().isRequireVerified();
    }

    private String ensureUnique(String base) {
        UniquenessStrategy strategy = properties.getUsername().getUniqueness().getStrategy();
        int maxAttempts = properties.getUsername().getUniqueness().getMaxAttempts();
        String sep = Objects.toString(properties.getUsername().getUniqueness().getSuffixSeparator(), "");
        int maxLen = properties.getUsername().getNormalize().getMaxLength();

        if (availabilityChecker.isAvailable(base)) return base;

        if (strategy == UniquenessStrategy.REJECT) {
            throw new IllegalArgumentException("Username already exists: " + base);
        }

        for (int i = 1; i <= Math.max(1, maxAttempts); i++) {
            String candidate;
            if (strategy == UniquenessStrategy.SUFFIX) {
                String sfx = sep + i;
                candidate = applySuffix(base, sfx, maxLen);
            } else {
                String sfx = sep + randomDigits(6);
                candidate = applySuffix(base, sfx, maxLen);
            }
            candidate = normalizeUsername(candidate);
            if (availabilityChecker.isAvailable(candidate)) return candidate;
        }

        throw new IllegalArgumentException("Could not generate unique username after " + maxAttempts + " attempts for base: " + base);
    }

    private String applySuffix(String base, String suffix, int maxLen) {
        if (maxLen <= 0) return base + suffix;
        int allowedBaseLen = Math.max(0, maxLen - suffix.length());
        String trimmed = base.length() > allowedBaseLen ? base.substring(0, allowedBaseLen) : base;
        return trimmed + suffix;
    }

    private String normalizeUsername(String raw) {
        AccountSettingsProperties.Username.Normalize n = properties.getUsername().getNormalize();

        String s = Objects.toString(raw, "");
        s = s.trim();

        if (n.isRemoveDiacritics()) {
            s = Normalizer.normalize(s, Normalizer.Form.NFD);
            s = s.replaceAll("\\p{M}+", "");
        }

        if (n.isLowercase()) {
            s = s.toLowerCase(Locale.ROOT);
        }

        String allowed = Objects.toString(n.getAllowedCharsRegex(), "");
        if (!isBlank(allowed)) {
            Pattern p = Pattern.compile(allowed);
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                String ch = String.valueOf(s.charAt(i));
                if (p.matcher(ch).matches()) out.append(ch);
            }
            s = out.toString();
        }

        int maxLength = n.getMaxLength();
        if (maxLength > 0 && s.length() > maxLength) {
            s = s.substring(0, maxLength);
        }

        if (isBlank(s)) {
            s = "user" + randomDigits(8);
            if (maxLength > 0 && s.length() > maxLength) s = s.substring(0, maxLength);
        }

        return s;
    }

    private String emailLocalPart(String email) {
        String e = Objects.toString(email, "").trim();
        int at = e.indexOf('@');
        return at > 0 ? e.substring(0, at) : e;
    }

    private String slugify(String text) {
        String s = Objects.toString(text, "").trim();
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        s = s.toLowerCase(Locale.ROOT);
        s = s.replaceAll("[^a-z0-9]+", "-");
        s = s.replaceAll("^-+|-+$", "");
        if (isBlank(s)) s = "user-" + randomDigits(8);
        return s;
    }

    private String buildPad(PadWith padWith, int padLength, int missing) {
        int len = Math.max(padLength, missing);
        if (padWith == PadWith.NONE || len <= 0) return "";
        if (padWith == PadWith.RANDOM_LETTERS) return randomLetters(len, LettersCase.LOWER);
        if (padWith == PadWith.RANDOM_DIGITS) return randomDigits(len);
        return randomLetters(len, LettersCase.LOWER) + randomDigits(len);
    }

    private String padChunk(String pad, int len) {
        if (len <= 0) return "";
        if (pad.length() >= len) return pad.substring(0, len);
        StringBuilder sb = new StringBuilder(pad);
        while (sb.length() < len) sb.append(pad);
        return sb.substring(0, len);
    }

    private String randomDigits(int count) {
        int c = Math.max(0, count);
        StringBuilder sb = new StringBuilder(c);
        for (int i = 0; i < c; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }

    private String randomLetters(int count, LettersCase lettersCase) {
        int c = Math.max(0, count);
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(c);
        for (int i = 0; i < c; i++) {
            char ch = alphabet.charAt(random.nextInt(alphabet.length()));
            if (lettersCase == LettersCase.UPPER) ch = Character.toUpperCase(ch);
            if (lettersCase == LettersCase.MIXED && random.nextBoolean()) ch = Character.toUpperCase(ch);
            sb.append(ch);
        }
        return sb.toString();
    }

    private String take(String value, int count) {
        String s = safe(value);
        int n = Math.max(0, count);
        return s.length() <= n ? s : s.substring(0, n);
    }

    private String joinNonBlank(String sep, String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (isBlank(p)) continue;
            if (!sb.isEmpty()) sb.append(sep);
            sb.append(p.trim());
        }
        return sb.toString();
    }

    private String requireNonBlank(String v, String message) {
        if (isBlank(v)) throw new IllegalArgumentException(message);
        return v;
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    private String safe(String v) {
        return v == null ? "" : v.trim();
    }
}
