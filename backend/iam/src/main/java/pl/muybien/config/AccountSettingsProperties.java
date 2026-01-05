package pl.muybien.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "settings.account")
public class AccountSettingsProperties {

    private Username username = new Username();
    private Email email = new Email();

    @Setter
    @Getter
    public static class Username {
        private UsernameMode mode = UsernameMode.PROVIDED;
        private Normalize normalize = new Normalize();
        private Uniqueness uniqueness = new Uniqueness();
        private Generation generation = new Generation();

        @Setter
        @Getter
        public static class Normalize {
            private boolean lowercase = true;
            private boolean removeDiacritics = true;
            private String allowedCharsRegex = "[a-z0-9._-]";
            private int maxLength = 30;

        }

        @Setter
        @Getter
        public static class Uniqueness {
            private UniquenessStrategy strategy = UniquenessStrategy.SUFFIX;
            private String suffixSeparator = "";
            private int maxAttempts = 20;

        }

        @Setter
        @Getter
        public static class Generation {
            private UsernameScheme scheme = UsernameScheme.NAME_BASED;
            private NameBased nameBased = new NameBased();

            @Setter
            @Getter
            public static class NameBased {
                private NamePart firstName = new NamePart();
                private NamePart lastName = new NamePart();
                private WhenTooShort whenTooShort = new WhenTooShort();
                private RandomPart random = new RandomPart();
                private String separator = "";

                @Setter
                @Getter
                public static class NamePart {
                    private int take = 3;

                }

                @Setter
                @Getter
                public static class WhenTooShort {
                    private PadWith padWith = PadWith.RANDOM_LETTERS;
                    private int padLength = 3;

                }

                @Setter
                @Getter
                public static class RandomPart {
                    private boolean appendDigits = true;
                    private int digitsCount = 3;
                    private boolean appendLetters = false;
                    private int lettersCount = 0;
                    private LettersCase lettersCase = LettersCase.LOWER;

                }
            }
        }
    }

    @Setter
    @Getter
    public static class Email {
        private EmailMode mode = EmailMode.PROVIDED;
        private boolean requireVerified = false;
        private Generation generation = new Generation();

        @Setter
        @Getter
        public static class Generation {
            private EmailScheme scheme = EmailScheme.DISABLED;
            private FromTemplate fromTemplate = new FromTemplate();

            @Setter
            @Getter
            public static class FromTemplate {
                private String template = "{username}@example.com";

            }
        }
    }

    public enum UsernameMode { PROVIDED, GENERATED, EMAIL }
    public enum UniquenessStrategy { REJECT, SUFFIX, RANDOM }
    public enum UsernameScheme { NAME_BASED, EMAIL_LOCALPART, UUID, SLUG }
    public enum PadWith { NONE, RANDOM_LETTERS, RANDOM_DIGITS, BOTH }
    public enum LettersCase { LOWER, UPPER, MIXED }
    public enum EmailMode { PROVIDED, GENERATED }
    public enum EmailScheme { DISABLED, FROM_USERNAME, FROM_TEMPLATE }
}
