package pl.muybien.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import pl.muybien.kafka.SubscriptionEmailConfirmation;

@Configuration
public class KafkaSubscriptionTopicConfig {

    @Bean
    public NewTopic subscriptionTopic() {
        return TopicBuilder
                .name("subscription-topic")
                .build();
    }

    @Bean
    public KafkaTemplate<String, SubscriptionEmailConfirmation> kafkaTemplate(
            ProducerFactory<String, SubscriptionEmailConfirmation> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
