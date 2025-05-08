package pl.muybien.kafka.confirmation;

import pl.muybien.enums.AlertType;
import pl.muybien.enums.TeamType;

public record SupportConfirmation(
        TeamType teamType,
        AlertType alertType,
        Object body
) {
}
