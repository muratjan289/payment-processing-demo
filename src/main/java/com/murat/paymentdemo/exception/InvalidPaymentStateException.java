package com.murat.paymentdemo.exception;

import com.murat.paymentdemo.domain.PaymentStatus;

public class InvalidPaymentStateException extends RuntimeException {



    public InvalidPaymentStateException(PaymentStatus currentStatus, PaymentStatus targetStatus) {

        super("Invalid payment state transition: " + currentStatus + " -> " + targetStatus);

    }

    public InvalidPaymentStateException(String message) {

        super(message);

    }
}
