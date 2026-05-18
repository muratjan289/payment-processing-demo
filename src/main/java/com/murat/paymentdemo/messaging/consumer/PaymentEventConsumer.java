package com.murat.paymentdemo.messaging.consumer;


import com.murat.paymentdemo.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class PaymentEventConsumer {

    @RabbitListener(queues = RabbitConfig.PAYMENT_EVENTS_QUEUE)
    public void handlePaymentEvent(Map<String, Object> event){
        log.info("Received Payment Event: {}", event);
    }


}
