package pl.muybien.autoconfig;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.muybien.security.InternalJwtAuthenticationConverters;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "internal-jwt", name = "public-key-pem")
@ConditionalOnClass(HttpSecurity.class)
@ConditionalOnBean(HttpSecurity.class)
public class InternalJwtServletSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain internalJwtSecurityFilterChain(
            HttpSecurity http,
            @Qualifier(InternalJwtServiceAutoConfiguration.INTERNAL_JWT_DECODER_BEAN) JwtDecoder internalJwtDecoder
    ) {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .decoder(internalJwtDecoder)
                        .jwtAuthenticationConverter(InternalJwtAuthenticationConverters.withUidPrincipalAndAuthorities())
                ));

        return http.build();
    }
}
