package com.quit.store.infrastructure.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private String bootstrapServers;
    private Consumer consumer = new Consumer();

    @Getter
    @Setter
    public static class Consumer {
        private String keyDeserializer;
        private String valueDeserializer;
    }

}
