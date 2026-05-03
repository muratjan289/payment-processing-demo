package com.murat.paymentdemo;

import org.springframework.boot.SpringApplication;

public class TestPaymentProcessingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.from(PaymentProcessingDemoApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
