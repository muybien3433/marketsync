package pl.muybien.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import pl.muybien.security.HeaderAuditorAware;

@AutoConfiguration
@ConditionalOnClass(name = {
        "jakarta.persistence.EntityManager",
        "org.springframework.data.jpa.domain.support.AuditingEntityListener",
        "org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect",
        "org.springframework.security.core.context.SecurityContextHolder"
})
@EnableJpaAuditing(auditorAwareRef = "headerAuditorAware")
public class SecurityAuditorAwareAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuditorAware.class)
    public AuditorAware<String> headerAuditorAware() {
        return new HeaderAuditorAware();
    }
}
