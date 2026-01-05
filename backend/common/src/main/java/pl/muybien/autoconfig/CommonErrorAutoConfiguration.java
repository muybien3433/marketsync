package pl.muybien.autoconfig;

import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import pl.muybien.exception.FeignErrorDecoder;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration
public class CommonErrorAutoConfiguration {

    @Bean
    @ConditionalOnClass(ErrorDecoder.class)
    @ConditionalOnMissingBean(ErrorDecoder.class)
    public ErrorDecoder errorDecoder(
            ObjectMapper objectMapper,
            @Value("${spring.application.name:unknown-service}") String appName
    ) {
        return new FeignErrorDecoder(objectMapper, appName);
    }
}
