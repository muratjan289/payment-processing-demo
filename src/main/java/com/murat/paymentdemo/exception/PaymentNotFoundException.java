package com.murat.paymentdemo.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(UUID paymentId) {
        super("Payment with id " + paymentId + " not found");
    }
}
