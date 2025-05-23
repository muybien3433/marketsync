package pl.muybien.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, Mono<JwtAuthenticationToken>> {

    @Override
    public Mono<JwtAuthenticationToken> convert(@NonNull Jwt source) {
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(
                source,
                Stream.concat(
                        new JwtGrantedAuthoritiesConverter().convert(source).stream(),
                        extractResourceRoles(source).stream()
                ).collect(Collectors.toSet())
        );
        return Mono.just(authenticationToken);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess != null && resourceAccess.containsKey("account")) {
            Object accountObject = resourceAccess.get("account");

            if (accountObject instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, List<String>> accountAccess = (Map<String, List<String>>) accountObject;

                List<String> roles = accountAccess.get("roles");

                if (roles != null) {
                    return roles.stream()
                            .map(role -> new SimpleGrantedAuthority(
                                    "ROLE_" + role.replace("-", "_").toUpperCase()))
                            .collect(Collectors.toSet());
                }
            }
        }
        return Set.of();
    }
}