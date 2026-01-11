package pl.muybien.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class HeaderAuditorAware implements AuditorAware<String> {

    private static final String CUSTOMER_ID_HEADER = "X-Customer-Id";
    private static final String FALLBACK = "SYSTEM";

    @Override
    public Optional<String> getCurrentAuditor() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return Optional.of(FALLBACK);
        }

        HttpServletRequest request = attrs.getRequest();
        String customerId = request.getHeader(CUSTOMER_ID_HEADER);

        if (customerId == null || customerId.isBlank()) {
            return Optional.of(FALLBACK);
        }

        return Optional.of(customerId);
    }
}
