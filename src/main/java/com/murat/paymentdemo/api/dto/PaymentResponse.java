package com.murat.paymentdemo.api.dto;

import com.murat.paymentdemo.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse (
    UUID paymentId,
    String customerId,
    BigDecimal amount,
    String currency,
    PaymentStatus status,
    Instant createdAt,
    Instant updatedAt
){
}
