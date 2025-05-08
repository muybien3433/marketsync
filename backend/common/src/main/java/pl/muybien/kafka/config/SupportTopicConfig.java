package pl.muybien.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import pl.muybien.kafka.confirmation.SupportConfirmation;

@Configuration
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class SupportTopicConfig {
    @Bean
    public NewTopic supportTopic() {
        return TopicBuilder
                .name("support-notification-topic")
                .build();
    }

    @Bean
    public KafkaTemplate<String, SupportConfirmation> supportKafkaTemplate(
            ProducerFactory<String, SupportConfirmation> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
