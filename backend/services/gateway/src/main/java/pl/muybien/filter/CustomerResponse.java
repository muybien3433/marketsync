package pl.muybien.filter;

import java.util.List;

public record CustomerResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String number,
        List<String> roles
) {
}
