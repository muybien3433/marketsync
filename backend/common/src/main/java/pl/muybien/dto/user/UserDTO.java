package pl.muybien.dto.user;

import java.time.LocalDate;
import java.util.UUID;

public record UserDTO(
        UUID id,
        UUID keycloakId,
        String username,
        String email,
        String firstName,
        String lastName,
        Boolean enabled,
        String createdBy,
        LocalDate createdDate,
        String lastModifiedBy,
        LocalDate lastModifiedDate
) {
}
