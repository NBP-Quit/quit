package com.quit.reservation.infrastructure.configuration;

import com.quit.reservation.infrastructure.messaging.message.PaymentMessage;
import com.quit.reservation.infrastructure.messaging.message.ReservationMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    private Map<String, Object> commonKafkaConfig() {
        Map<String, Object> kafkaConfig = new HashMap<>();
        kafkaConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        kafkaConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        kafkaConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        kafkaConfig.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        kafkaConfig.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return kafkaConfig;
    }

    private <T> ConsumerFactory<String, T> createConsumerFactory(Class<T> valueType) {
        Map<String, Object> kafkaConfig = commonKafkaConfig();
        kafkaConfig.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueType.getName());
        return new DefaultKafkaConsumerFactory<>(kafkaConfig, new StringDeserializer(), new JsonDeserializer<>(valueType));
    }

    @Bean
    public ConsumerFactory<String, PaymentMessage> paymentConsumerFactory() {
        return createConsumerFactory(PaymentMessage.class);
    }

    @Bean
    public ConsumerFactory<String, ReservationMessage> queueConsumerFactory() {
        return createConsumerFactory(ReservationMessage.class);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> createKafkaListenerContainerFactory(ConsumerFactory<String, T> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentMessage> paymentKafkaListenerContainerFactory() {
        return createKafkaListenerContainerFactory(paymentConsumerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReservationMessage> queueKafkaListenerContainerFactory() {
        return createKafkaListenerContainerFactory(queueConsumerFactory());
    }
}

