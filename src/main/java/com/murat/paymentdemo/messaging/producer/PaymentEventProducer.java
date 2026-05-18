package com.murat.paymentdemo.messaging.producer;

import com.murat.paymentdemo.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {


    private final RabbitTemplate rabbitTemplate;

    public void  publishPaymentCreated(Object event){
        rabbitTemplate.convertAndSend(
                RabbitConfig.PAYMENT_EXCHANGE,
                RabbitConfig.PAYMENT_CREATED_ROUTING_KEY,
                event
        );
    }

    public void publishPaymentConfirmed(Object event){
        rabbitTemplate.convertAndSend(
                RabbitConfig.PAYMENT_EXCHANGE,
                RabbitConfig.PAYMENT_CONFIRMED_ROUTING_KEY,
                event
        );
    }

    public void publishPaymentCancelled(Object event){
        rabbitTemplate.convertAndSend(
                RabbitConfig.PAYMENT_EXCHANGE,
                RabbitConfig.PAYMENT_CANCELLED_ROUTING_KEY,
                event
        );
    }
}
