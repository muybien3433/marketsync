package pl.muybien.feign;

import io.platform.dto.iam.request.KeycloakUserCreateRequest;
import io.platform.dto.iam.request.KeycloakUserLoginRequest;
import io.platform.dto.iam.response.KeycloakUserCreatedResponse;
import io.platform.dto.iam.response.KeycloakUserLoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "iam-service",
        url = "${application.config.iam-url}"
)
public interface IamClient {

    @PostMapping("/users")
    KeycloakUserCreatedResponse register(@RequestBody KeycloakUserCreateRequest request);

    @PostMapping("/auth/login")
    KeycloakUserLoginResponse login(@RequestBody KeycloakUserLoginRequest request);

    @DeleteMapping("/users/{id}")
    void delete(@PathVariable(name = "id") String id);
}
