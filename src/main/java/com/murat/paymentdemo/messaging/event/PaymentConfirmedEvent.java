package com.murat.paymentdemo.messaging.event;

import com.murat.paymentdemo.domain.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

public record PaymentConfirmedEvent(
        UUID paymentId,
        PaymentStatus status,
        Instant occurredAt
) {
}
