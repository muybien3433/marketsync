package io.platform.config;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import io.platform.security.InternalJwtProperties;
import io.platform.security.RsaPemKeys;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableConfigurationProperties(InternalJwtProperties.class)
public class InternalJwtGatewayConfig {

    @Bean
    public RSAPublicKey internalJwtPublicKey(InternalJwtProperties props) {
        return RsaPemKeys.readPublicKey(props.publicKeyPem());
    }

    @Bean
    public RSAPrivateKey internalJwtPrivateKey(InternalJwtProperties props) {
        return RsaPemKeys.readPrivateKey(props.privateKeyPem());
    }

    @Bean
    public JwtEncoder internalJwtEncoder(InternalJwtProperties props, RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        RSAKey.Builder builder = new RSAKey.Builder(publicKey).privateKey(privateKey);
        if (props.keyId() != null && !props.keyId().isBlank()) builder.keyID(props.keyId());

        RSAKey rsaKey = builder.build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }
}
