package pl.muybien.mapper.user;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import pl.muybien.dto.user.request.UserRegisterRequest;
import pl.muybien.entity.user.User;
import pl.muybien.entity.user.UserConfig;

@Component
public class UserMapper {

    public User toEntity(UserRegisterRequest request, String keycloakId) {
        User user = User.builder()
                .keycloakId(keycloakId)
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
