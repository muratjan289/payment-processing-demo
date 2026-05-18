package com.murat.paymentdemo.messaging.event;

import com.murat.paymentdemo.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentCreatedEvent(
        UUID paymentId,
        String customerId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        Instant occurredAt
) {
}
