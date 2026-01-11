package pl.muybien.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class InternalJwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        Set<String> authorities = new LinkedHashSet<>();

        authorities.addAll(readStringList(jwt.getClaims().get("authorities")));

        for (String role : readStringList(jwt.getClaims().get("roles"))) {
            authorities.add("ROLE_" + role.replace("-", "_").toUpperCase());
        }

        return authorities.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<String> readStringList(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().filter(String.class::isInstance).map(String.class::cast).toList();
        }
        if (value instanceof String s && !s.isBlank()) {
            return Arrays.stream(s.split(",")).map(String::trim).filter(x -> !x.isBlank()).toList();
        }
        return List.of();
    }
}
