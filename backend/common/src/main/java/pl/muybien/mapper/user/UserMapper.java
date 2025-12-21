package pl.muybien.mapper.user;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import pl.muybien.dto.user.request.UserRegisterRequest;
import pl.muybien.entity.user.User;

@Component
public class UserMapper {
    public User toEntity(UserRegisterRequest request, String keycloakId) {
        return User.builder()
                .keycloakId(keycloakId)
                .language(request.language())
                .currency(request.currency())
                .build();
    }
}
