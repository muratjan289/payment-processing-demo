package com.murat.paymentdemo.api;


import com.murat.paymentdemo.api.dto.CreatePaymentRequest;
import com.murat.paymentdemo.api.dto.PaymentHistoryResponse;
import com.murat.paymentdemo.api.dto.PaymentResponse;
import com.murat.paymentdemo.api.dto.PaymentStatusResponse;
import com.murat.paymentdemo.application.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/payments")
@RequiredArgsConstructor
public class PaymentController {


    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/{paymentId}")
    public PaymentStatusResponse getPayment(@PathVariable UUID paymentId){
        return paymentService.getPayment(paymentId);
    }

    @PostMapping("/{paymentId}/confirm")
    public PaymentStatusResponse confirmPayment(@PathVariable UUID paymentId){
        return paymentService.confirmPayment(paymentId);
    }

    @PostMapping("/{paymentId}/cancel")
    public PaymentStatusResponse cancelPayment(@PathVariable UUID paymentId){
        return paymentService.cancelPayment(paymentId);
    }

    @GetMapping("/{paymentId}/history")
    public List<PaymentHistoryResponse> getPaymentHistory(@PathVariable UUID paymentId){
        return paymentService.getPaymentHistory(paymentId);
    }




}
