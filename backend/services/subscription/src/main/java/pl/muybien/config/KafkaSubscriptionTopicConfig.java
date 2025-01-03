package pl.muybien.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaSubscriptionTopicConfig {

    @Bean
    public NewTopic subscriptionTopic() {
        return TopicBuilder
                .name("subscription-topic")
                .build();
    }
}
