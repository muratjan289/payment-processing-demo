package com.murat.paymentdemo.api.dto;

import com.murat.paymentdemo.domain.PaymentStatus;

import java.util.UUID;

public record PaymentStatusResponse(
        UUID paymentId,
        PaymentStatus status
) {
}
