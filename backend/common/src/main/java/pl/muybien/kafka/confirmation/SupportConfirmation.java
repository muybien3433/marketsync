package pl.muybien.kafka.confirmation;

import pl.muybien.enumeration.AlertType;
import pl.muybien.enumeration.TeamType;

public record SupportConfirmation(
        TeamType teamType,
        AlertType alertType,
        Object body
) {
}
