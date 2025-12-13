package pl.muybien.websocket;

import pl.muybien.enumeration.CurrencyType;
import java.util.UUID;

public record WalletSubscription(UUID customerId, CurrencyType currencyType) {
}
