//package pl.muybien.config;
//
//import feign.codec.Encoder;
//import org.springframework.beans.factory.ObjectProvider;
//import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
//import org.springframework.cloud.openfeign.support.SpringEncoder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FeignSupportConfig {
//
//    @Bean
//    public Encoder feignEncoder(ObjectProvider<FeignHttpMessageConverters> messageConverters) {
//        return new SpringEncoder(messageConverters);
//    }
//}
