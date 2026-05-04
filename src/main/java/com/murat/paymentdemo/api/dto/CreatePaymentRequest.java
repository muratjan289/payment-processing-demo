package com.murat.paymentdemo.api.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePaymentRequest (

    @NotBlank(message = "customerId must not be blank")
    @Size(max = 100, message = "customerId must not exceed 100 characters")
    String customerId,

    @NotNull(message = "amount must not be null")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    BigDecimal amount,

    @NotBlank(message = "currency must not be blank")
    @Size(min = 3, max = 3, message = "currency must be a 3 letter ISO code")
    String currency,

    @NotBlank(message = "idempotencyKey must not be blank")
            @Size(max = 150, message = "idempotencyKey must not exceed 150 characters")
    String idempotencyKey
)
{
}
