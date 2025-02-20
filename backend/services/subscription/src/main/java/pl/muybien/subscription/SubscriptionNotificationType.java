package pl.muybien.subscription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.muybien.exception.InvalidSubscriptionParametersException;

@Getter
@RequiredArgsConstructor
public enum SubscriptionNotificationType {
    EMAIL("email");

    private final String value;

    public static SubscriptionNotificationType findByValue(String value) {
        for (SubscriptionNotificationType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new InvalidSubscriptionParametersException(
                "Subscription assetType not recognized: " + value);
    }
}
