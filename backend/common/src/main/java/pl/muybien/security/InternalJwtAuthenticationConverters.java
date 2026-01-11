package pl.muybien.security;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public final class InternalJwtAuthenticationConverters {

    private InternalJwtAuthenticationConverters() {}

    public static JwtAuthenticationConverter withUidPrincipalAndAuthorities() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("uid");
        converter.setJwtGrantedAuthoritiesConverter(new InternalJwtAuthoritiesConverter());
        return converter;
    }
}
