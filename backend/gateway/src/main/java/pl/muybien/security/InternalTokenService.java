package pl.muybien.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import pl.muybien.filter.CustomerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InternalTokenService {

    private final JwtEncoder internalJwtEncoder;
    private final InternalJwtProperties props;

    public Mono<String> mint(String audience, CustomerResponse customer) {
        return Mono.fromCallable(() -> {
            Instant now = Instant.now();
            Instant exp = now.plusSeconds(props.ttlSeconds() == null ? 90L : props.ttlSeconds());

            JwtClaimsSet.Builder b = JwtClaimsSet.builder()
                    .issuer(props.issuer())
                    .issuedAt(now)
                    .expiresAt(exp)
                    .id(UUID.randomUUID().toString())
                    .audience(List.of(audience))
                    .subject("gateway");

            if (customer != null) {
                if (customer.id() != null) b.claim("uid", customer.id());
                if (customer.email() != null) b.claim("email", customer.email());
                if (customer.number() != null) b.claim("number", customer.number());
                if (customer.firstName() != null) b.claim("given_name", customer.firstName());
                if (customer.lastName() != null) b.claim("family_name", customer.lastName());
                if (customer.roles() != null) b.claim("roles", customer.roles());
            }

            JwtClaimsSet claims = b.build();
            return internalJwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
