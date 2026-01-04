package com.project.paymentservice.conf;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for KafkaConfig.
 * Tests Kafka topic bean creation with Spring context.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "kafka.topic.payment-events=payment-events"
})
class KafkaConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void paymentEventsTopic_shouldBeCreatedAsBean() {
        // Act
        boolean beanExists = applicationContext.containsBean("paymentEventsTopic");

        // Assert
        assertThat(beanExists).isTrue();
    }

    @Test
    void paymentEventsTopic_shouldHaveCorrectName() {
        // Act
        NewTopic topic = applicationContext.getBean("paymentEventsTopic", NewTopic.class);

        // Assert
        assertThat(topic).isNotNull();
        assertThat(topic.name()).isEqualTo("payment-events");
    }

    @Test
    void paymentEventsTopic_shouldHaveThreePartitions() {
        // Act
        NewTopic topic = applicationContext.getBean("paymentEventsTopic", NewTopic.class);

        // Assert
        assertThat(topic.numPartitions()).isEqualTo(3);
    }

    @Test
    void paymentEventsTopic_shouldHaveReplicationFactorOfOne() {
        // Act
        NewTopic topic = applicationContext.getBean("paymentEventsTopic", NewTopic.class);

        // Assert
        assertThat(topic.replicationFactor()).isEqualTo((short) 1);
    }

    @Test
    void paymentEventsTopic_shouldBeConfiguredCorrectly() {
        // Act
        NewTopic topic = applicationContext.getBean("paymentEventsTopic", NewTopic.class);

        // Assert
        assertThat(topic).isNotNull();
        assertThat(topic.name()).isEqualTo("payment-events");
        assertThat(topic.numPartitions()).isEqualTo(3);
        assertThat(topic.replicationFactor()).isEqualTo((short) 1);
    }
}
