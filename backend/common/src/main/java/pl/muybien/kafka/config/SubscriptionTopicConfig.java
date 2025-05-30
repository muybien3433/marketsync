package pl.muybien.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import pl.muybien.kafka.confirmation.SubscriptionConfirmation;

@Configuration
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class SubscriptionTopicConfig {

    @Bean
    public NewTopic subscriptionTopic() {
        return TopicBuilder
                .name("subscription-notification-topic")
                .build();
    }

    @Bean
    public KafkaTemplate<String, SubscriptionConfirmation> subscriptionKafkaTemplate(
            ProducerFactory<String, SubscriptionConfirmation> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
