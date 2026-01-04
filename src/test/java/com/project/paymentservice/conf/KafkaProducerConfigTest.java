package com.project.paymentservice.conf;

import com.project.paymentservice.model.dto.event.PaymentEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for KafkaProducerConfig.
 * Tests Kafka producer factory and template configuration with Spring context.
 */
@SpringBootTest
class KafkaProducerConfigTest {

    @Autowired
    private ProducerFactory<String, PaymentEvent> producerFactory;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Test
    void producerFactory_shouldBeInjected() {
        // Assert
        assertThat(producerFactory).isNotNull();
    }

    @Test
    void kafkaTemplate_shouldBeInjected() {
        // Assert
        assertThat(kafkaTemplate).isNotNull();
    }

    @Test
    void producerFactory_shouldHaveCorrectBootstrapServers() {
        // Act
        Map<String, Object> config = producerFactory.getConfigurationProperties();

        // Assert
        assertThat(config).containsKey(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG);
        assertThat(config.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG))
                .isEqualTo("localhost:9092");
    }

    @Test
    void producerFactory_shouldHaveStringKeySerializer() {
        // Act
        Map<String, Object> config = producerFactory.getConfigurationProperties();

        // Assert
        assertThat(config).containsKey(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG);
        assertThat(config.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG))
                .isEqualTo(StringSerializer.class);
    }

    @Test
    void producerFactory_shouldHaveJsonValueSerializer() {
        // Act
        Map<String, Object> config = producerFactory.getConfigurationProperties();

        // Assert
        assertThat(config).containsKey(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG);
        assertThat(config.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG))
                .isEqualTo(JsonSerializer.class);
    }

    @Test
    void producerFactory_shouldHaveTypeHeadersEnabled() {
        // Act
        Map<String, Object> config = producerFactory.getConfigurationProperties();

        // Assert
        assertThat(config).containsKey(JsonSerializer.ADD_TYPE_INFO_HEADERS);
        assertThat(config.get(JsonSerializer.ADD_TYPE_INFO_HEADERS))
                .isEqualTo(true);
    }

    @Test
    void producerFactory_shouldBeConfiguredCorrectly() {
        // Act
        Map<String, Object> config = producerFactory.getConfigurationProperties();

        // Assert - Comprehensive configuration check
        assertThat(config).isNotNull();
        assertThat(config.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG))
                .isEqualTo("localhost:9092");
        assertThat(config.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG))
                .isEqualTo(StringSerializer.class);
        assertThat(config.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG))
                .isEqualTo(JsonSerializer.class);
        assertThat(config.get(JsonSerializer.ADD_TYPE_INFO_HEADERS))
                .isEqualTo(true);
    }

    @Test
    void kafkaTemplate_shouldUseConfiguredProducerFactory() {
        // Act
        ProducerFactory<String, PaymentEvent> templateFactory = kafkaTemplate.getProducerFactory();

        // Assert
        assertThat(templateFactory).isNotNull();
        assertThat(templateFactory).isSameAs(producerFactory);
    }

    @Test
    void kafkaTemplate_shouldBeReadyForProducing() {
        // Assert - Verify template is configured and ready
        assertThat(kafkaTemplate).isNotNull();
        assertThat(kafkaTemplate.getProducerFactory()).isNotNull();
        assertThat(kafkaTemplate.getDefaultTopic()).isNull(); // No default topic set
    }
}
