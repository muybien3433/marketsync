package pl.muybien.subscription.config;

import feign.codec.Decoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class FeignWebFluxConfig {

    @Bean
    public Decoder feignDecoder() {
        return new SpringDecoder(() -> new org.springframework.boot.autoconfigure.http.HttpMessageConverters(
                new MappingJackson2HttpMessageConverter()));
    }
}
