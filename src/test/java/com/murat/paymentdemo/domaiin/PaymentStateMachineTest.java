package com.murat.paymentdemo.domaiin;

import com.murat.paymentdemo.domain.PaymentStateMachine;
import com.murat.paymentdemo.domain.PaymentStatus;
import com.murat.paymentdemo.exception.InvalidPaymentStateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaymentStateMachineTest {

    @Test
    void shouldAllowPendingToConfirmedTransition(){
        assertDoesNotThrow(() ->
                        PaymentStateMachine.validateTransition(
                                PaymentStatus.PENDING,
                                PaymentStatus.CONFIRMED
                        )
                );
    }

    @Test
    void shouldAllowPendingToCancelledTransition(){
        assertDoesNotThrow(() ->
                PaymentStateMachine.validateTransition(
                        PaymentStatus.PENDING,
                        PaymentStatus.CANCELLED
                )
        );
    }

    @Test
    void shouldRejectConfirmedToCancelledTransition(){
        assertThrows(
                InvalidPaymentStateException.class,
                () -> PaymentStateMachine.validateTransition(
                        PaymentStatus.CONFIRMED,
                        PaymentStatus.CANCELLED
                )
        );
    }

    @Test
    void shouldRejectCancelledToConfirmedTransition(){
        assertThrows(
                InvalidPaymentStateException.class,
                () -> PaymentStateMachine.validateTransition(
                        PaymentStatus.CANCELLED,
                        PaymentStatus.CONFIRMED
                )
        );
    }
}
