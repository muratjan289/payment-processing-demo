package com.murat.paymentdemo.application;


import com.murat.paymentdemo.messaging.event.PaymentCancelledEvent;
import com.murat.paymentdemo.messaging.event.PaymentConfirmedEvent;
import com.murat.paymentdemo.messaging.event.PaymentCreatedEvent;
import com.murat.paymentdemo.messaging.producer.PaymentEventProducer;
import com.murat.paymentdemo.persistence.entity.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentEventService {

    private final PaymentEventProducer paymentEventProducer;

    public void publishPaymentCreated(PaymentEntity payment) {

        PaymentCreatedEvent event = new PaymentCreatedEvent(
        payment.getId(),
        payment.getCustomerId(),
        payment.getAmount(),
        payment.getCurrency(),
        payment.getStatus(),
                Instant.now()
        );
        paymentEventProducer.publishPaymentCreated(event);
    }


    public void publishPaymentConfirmed(PaymentEntity payment) {

        PaymentConfirmedEvent event = new PaymentConfirmedEvent(
                payment.getId(),
                payment.getStatus(),
                Instant.now()
        );

        paymentEventProducer.publishPaymentConfirmed(event);
    }

    public void publishPaymentDeleted(PaymentEntity payment) {

        PaymentCancelledEvent event = new  PaymentCancelledEvent(
        payment.getId(),
        payment.getStatus(),
                Instant.now()
        );
        paymentEventProducer.publishPaymentCancelled(event);
    }





}
