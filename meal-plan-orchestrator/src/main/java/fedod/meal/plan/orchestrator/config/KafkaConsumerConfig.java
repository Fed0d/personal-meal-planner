package fedod.meal.plan.orchestrator.config;

import fedod.meal.plan.orchestrator.dto.kafka.JobUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final Environment environment;

    @Bean
    public ConsumerFactory<String, JobUpdatedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.consumer.bootstrap-servers"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                environment.getProperty("spring.kafka.consumer.group-id"));

        var jackson = new JacksonJsonDeserializer<>(JobUpdatedEvent.class, false);
        var ehd = new ErrorHandlingDeserializer<>(jackson);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), ehd);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JobUpdatedEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, JobUpdatedEvent> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, JobUpdatedEvent>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
