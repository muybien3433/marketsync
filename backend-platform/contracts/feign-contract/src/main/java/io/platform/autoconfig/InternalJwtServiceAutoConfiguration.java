package io.platform.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import io.platform.security.AudienceValidator;
import io.platform.security.InternalJwtProperties;
import io.platform.security.RsaPemKeys;

import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableConfigurationProperties(InternalJwtProperties.class)
@ConditionalOnProperty(prefix = "internal-jwt", name = "public-key-pem")
public class InternalJwtServiceAutoConfiguration {

    public static final String INTERNAL_JWT_DECODER_BEAN = "internalJwtDecoder";

    @Bean
    @ConditionalOnMissingBean
    public RSAPublicKey internalJwtPublicKey(InternalJwtProperties props) {
        return RsaPemKeys.readPublicKey(props.publicKeyPem());
    }

    @Bean(name = INTERNAL_JWT_DECODER_BEAN)
    @ConditionalOnMissingBean(name = INTERNAL_JWT_DECODER_BEAN)
    public JwtDecoder internalJwtDecoder(RSAPublicKey internalJwtPublicKey, InternalJwtProperties props) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(internalJwtPublicKey).build();

        OAuth2TokenValidator<Jwt> withTimestamp = JwtValidators.createDefault();
        OAuth2TokenValidator<Jwt> withIssuer =
                (props.issuer() == null || props.issuer().isBlank())
                        ? _ -> OAuth2TokenValidatorResult.success()
                        : JwtValidators.createDefaultWithIssuer(props.issuer());
        OAuth2TokenValidator<Jwt> withAudience = new AudienceValidator(props.audience());

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withTimestamp, withIssuer, withAudience));

        return decoder;
    }

}
