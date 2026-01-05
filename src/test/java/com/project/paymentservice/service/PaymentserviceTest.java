package com.project.paymentservice.service;

import com.project.paymentservice.model.dto.event.PaymentEvent;
import com.project.paymentservice.testutil.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Paymentservice.
 * Tests Kafka message sending functionality with mocked KafkaTemplate.
 */
@ExtendWith(MockitoExtension.class)
class PaymentserviceTest {

    private static final String PAYMENT_EVENTS_TOPIC = "payment-events";

    @Mock
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @InjectMocks
    private Paymentservice paymentservice;

    private PaymentEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = TestDataBuilder.createPaymentEvent();
    }

    @Test
    void sendPaymentEvent_shouldSendEventToKafka_whenValidEventProvided() {
        // Arrange
        PaymentEvent event = PaymentEvent.builder()
                .clientEmail("test@example.com")
                .clientName("Test Client")
                .amount(BigDecimal.valueOf(1500.50))
                .clientPhoneNumber("9876543210")
                .description("Test virement")
                .build();

        // Act
        paymentservice.sendPaymentEvent(event);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(PAYMENT_EVENTS_TOPIC), eq(event));
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void sendPaymentEvent_shouldSendEventToCorrectTopic_whenCalled() {
        // Act
        paymentservice.sendPaymentEvent(testEvent);

        // Assert
        verify(kafkaTemplate).send(eq(PAYMENT_EVENTS_TOPIC), eq(testEvent));
    }

    @Test
    void sendPaymentEvent_shouldHandleNullEvent_withoutException() {
        // Arrange
        PaymentEvent nullEvent = null;

        // Act
        paymentservice.sendPaymentEvent(nullEvent);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(PAYMENT_EVENTS_TOPIC), isNull());
    }

    @Test
    void sendPaymentEvent_shouldHandleEventWithNullFields_withoutException() {
        // Arrange
        PaymentEvent eventWithNullFields = TestDataBuilder.createPaymentEventWithNullFields();

        // Act
        paymentservice.sendPaymentEvent(eventWithNullFields);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(PAYMENT_EVENTS_TOPIC), eq(eventWithNullFields));
    }

    @Test
    void sendPaymentEvent_shouldSendMultipleEvents_whenCalledMultipleTimes() {
        // Arrange
        PaymentEvent event1 = TestDataBuilder.createPaymentEvent("user1@test.com", "User One",
                BigDecimal.valueOf(100), "1111111111", "Payment 1");
        PaymentEvent event2 = TestDataBuilder.createPaymentEvent("user2@test.com", "User Two",
                BigDecimal.valueOf(200), "2222222222", "Payment 2");
        PaymentEvent event3 = TestDataBuilder.createPaymentEvent("user3@test.com", "User Three",
                BigDecimal.valueOf(300), "3333333333", "Payment 3");

        // Act
        paymentservice.sendPaymentEvent(event1);
        paymentservice.sendPaymentEvent(event2);
        paymentservice.sendPaymentEvent(event3);

        // Assert
        verify(kafkaTemplate, times(3)).send(eq(PAYMENT_EVENTS_TOPIC), any(PaymentEvent.class));
        verify(kafkaTemplate).send(PAYMENT_EVENTS_TOPIC, event1);
        verify(kafkaTemplate).send(PAYMENT_EVENTS_TOPIC, event2);
        verify(kafkaTemplate).send(PAYMENT_EVENTS_TOPIC, event3);
    }

    @Test
    void sendPaymentEvent_shouldSendEventWithZeroAmount_withoutException() {
        // Arrange
        PaymentEvent zeroAmountEvent = TestDataBuilder.createPaymentEvent(
                "zero@test.com",
                "Zero Amount",
                BigDecimal.ZERO,
                "0000000000",
                "Zero amount payment");

        // Act
        paymentservice.sendPaymentEvent(zeroAmountEvent);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(PAYMENT_EVENTS_TOPIC), eq(zeroAmountEvent));
    }

    @Test
    void sendPaymentEvent_shouldSendEventWithNegativeAmount_withoutException() {
        // Arrange
        PaymentEvent negativeAmountEvent = TestDataBuilder.createPaymentEvent(
                "negative@test.com",
                "Negative Amount",
                BigDecimal.valueOf(-100),
                "9999999999",
                "Refund payment");

        // Act
        paymentservice.sendPaymentEvent(negativeAmountEvent);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(PAYMENT_EVENTS_TOPIC), eq(negativeAmountEvent));
    }

    @Test
    void sendPaymentEvent_shouldSendEventWithLargeAmount_withoutException() {
        // Arrange
        PaymentEvent largeAmountEvent = TestDataBuilder.createPaymentEvent(
                "large@test.com",
                "Large Amount",
                new BigDecimal("999999999.99"),
                "5555555555",
                "Large payment");

        // Act
        paymentservice.sendPaymentEvent(largeAmountEvent);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(PAYMENT_EVENTS_TOPIC), eq(largeAmountEvent));
    }
}
