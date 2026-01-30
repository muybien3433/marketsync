package io.platform.autoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import io.platform.exception.FeignErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CommonFeignErrorAutoConfiguration {

    @Bean
    @ConditionalOnClass(name = "feign.codec.ErrorDecoder")
    public ErrorDecoder errorDecoder(
            ObjectMapper objectMapper,
            @Value("${spring.application.name:unknown-service}") String appName
    ) {
        return new FeignErrorDecoder(objectMapper, appName);
    }
}
