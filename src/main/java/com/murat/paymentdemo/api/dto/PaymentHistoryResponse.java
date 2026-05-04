package com.murat.paymentdemo.api.dto;

import com.murat.paymentdemo.domain.PaymentStatus;

import java.time.Instant;

public record PaymentHistoryResponse(
        PaymentStatus oldStatus,
        PaymentStatus newStatus,
        Instant changedAt
) {


}
