package com.quit.queue.application.service.infrastructure.messaging;

import com.quit.queue.infrastructure.messaging.KafkaMessageProducer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unchecked")
class KafkaMessageProducerTest {

    static class TestMessage {
        private String message1;
        private String message2;
        private int message3;

        public TestMessage(String message1, String message2, int message3) {
            this.message1 = message1;
            this.message2 = message2;
            this.message3 = message3;
        }

        public String getMessage1() {
            return message1;
        }

        public void setMessage1(String message1) {
            this.message1 = message1;
        }

        public String getMessage2() {
            return message2;
        }

        public void setMessage2(String message2) {
            this.message2 = message2;
        }

        public int getMessage3() {
            return message3;
        }

        public void setMessage3(int message3) {
            this.message3 = message3;
        }
    }

    @Test
    void sendMessage() {
        // Mock KafkaTemplate
        KafkaTemplate<String, Object> kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        KafkaMessageProducer kafkaMessageProducer = new KafkaMessageProducer(kafkaTemplate);

        String topic = "test-topic";
        String key = "key1";

        // TestMessage 객체를 테스트 코드 내에서만 생성
        TestMessage testMessage = new TestMessage("John Doe", "Male", 30);

        // Act: Call the method under test
        kafkaMessageProducer.sendMessage(topic, key, testMessage);

        // Assert: Verify that the send method was called with the correct parameters
        verify(kafkaTemplate, times(1)).send(topic, key, testMessage);
    }
}