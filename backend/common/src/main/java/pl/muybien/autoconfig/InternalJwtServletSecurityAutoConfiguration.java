package pl.muybien.autoconfig;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import pl.muybien.security.InternalJwtAuthenticationConverters;
import pl.muybien.security.InternalJwtProperties;

@AutoConfiguration
@EnableConfigurationProperties(InternalJwtProperties.class)
@ConditionalOnProperty(prefix = "internal-jwt", name = "public-key-pem")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(HttpSecurity.class)
@ConditionalOnBean(HttpSecurity.class)
public class InternalJwtServletSecurityAutoConfiguration {

    @Bean
    @Order(0)
    @ConditionalOnMissingBean(name = "internalJwtSecurityFilterChain")
    public SecurityFilterChain internalJwtSecurityFilterChain(
            HttpSecurity http,
            @Qualifier(InternalJwtServiceAutoConfiguration.INTERNAL_JWT_DECODER_BEAN) JwtDecoder internalJwtDecoder) {

        http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(RequestCacheConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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
