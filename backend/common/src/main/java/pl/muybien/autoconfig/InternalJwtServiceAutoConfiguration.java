package pl.muybien.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;
import pl.muybien.security.InternalJwtProperties;
import pl.muybien.security.RsaPemKeys;

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
}
