package pl.muybien.mapper;

import org.springframework.stereotype.Component;
import pl.muybien.dto.request.UserRegisterRequest;
import pl.muybien.entity.user.User;
import pl.muybien.entity.user.UserConfig;

import java.util.UUID;

@Component
public class UserMapper {

    public User toEntity(UserRegisterRequest request, String keycloakId) {
        User user = User.builder()
                .keycloakId(UUID.fromString(keycloakId))
                .build();

        UserConfig config = UserConfig.builder()
                .language(request.language())
                .currency(request.currency())
                .user(user)
                .build();

        user.setUserConfig(config);

        return user;
    }
}
