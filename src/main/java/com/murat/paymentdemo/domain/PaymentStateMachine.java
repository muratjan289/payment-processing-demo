package com.murat.paymentdemo.domain;

import com.murat.paymentdemo.exception.InvalidPaymentStateException;

public class PaymentStateMachine {

    public PaymentStateMachine() {
    }

    public static void validateTransition(PaymentStatus currentStatus, PaymentStatus targetStatus) {
        if(!isTransitionAllowed(currentStatus, targetStatus)){
            throw new InvalidPaymentStateException(currentStatus,targetStatus);
        }
    }

    private static boolean isTransitionAllowed(PaymentStatus currentStatus, PaymentStatus targetStatus) {
        return currentStatus == PaymentStatus.PENDING &&
                (
                targetStatus == PaymentStatus.CONFIRMED ||
                targetStatus == PaymentStatus.CANCELLED ||
                targetStatus == PaymentStatus.FAILED
        );

    }
}
