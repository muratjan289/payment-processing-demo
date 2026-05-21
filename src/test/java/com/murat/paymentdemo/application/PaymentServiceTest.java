package com.murat.paymentdemo.application;


import com.murat.paymentdemo.api.dto.CreatePaymentRequest;
import com.murat.paymentdemo.api.dto.PaymentResponse;
import com.murat.paymentdemo.api.mapper.PaymentMapper;
import com.murat.paymentdemo.domain.PaymentStatus;
import com.murat.paymentdemo.persistence.entity.PaymentEntity;
import com.murat.paymentdemo.persistence.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentHistoryService paymentHistoryService;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    PaymentEventService paymentEventService;

    @InjectMocks
    private PaymentService paymentService;

    private CreatePaymentRequest request;

    @BeforeEach
    void setUp() {
        request = new CreatePaymentRequest(
                "cust-123",
                BigDecimal.valueOf(1500),
                "USD",
                "pay-req-001"

        );
    }

    @Test
    void shouldCreateNewPaymentWhenIdempotencyKeyDoesNotExist() {
        PaymentEntity savedPayment = createPaymentEntity();

        PaymentResponse expectedResponse = new PaymentResponse(
                savedPayment.getId(),
                savedPayment.getCustomerId(),
                savedPayment.getAmount(),
                savedPayment.getCurrency(),
                savedPayment.getStatus(),
                savedPayment.getCreatedAt(),
                savedPayment.getUpdatedAt()
        );
        when(idempotencyService.findPaymentId(request.idempotencyKey()))
                .thenReturn(Optional.empty());

        when(paymentRepository.findByIdempotencyKey(request.idempotencyKey()))
                .thenReturn(Optional.empty());

        when(paymentRepository.save(any(PaymentEntity.class)))
                .thenReturn(savedPayment);

        when(paymentMapper.toPaymentResponse(savedPayment))
                .thenReturn(expectedResponse);

        PaymentResponse actualResponse = paymentService.createPayment(request);

        assertEquals(expectedResponse, actualResponse);

        verify(paymentRepository).save(any(PaymentEntity.class));

        verify(paymentHistoryService).recordStatusChange(
                savedPayment.getId(),
                null,
                PaymentStatus.PENDING
        );

        verify(idempotencyService).savePaymentId(
                request.idempotencyKey(),
                savedPayment.getId()

        );
        verify(paymentEventService).publishPaymentCreated(savedPayment);
    }



    @Test
    void shouldReturnExistingPaymentWhenIdempotencyKeyExistsInRedis() {
        PaymentEntity existingPayment = createPaymentEntity();

        PaymentResponse expectedResponse = new PaymentResponse(
                existingPayment.getId(),
                existingPayment.getCustomerId(),
                existingPayment.getAmount(),
                existingPayment.getCurrency(),
                existingPayment.getStatus(),
                existingPayment.getCreatedAt(),
                existingPayment.getUpdatedAt()

        );

        when(idempotencyService.findPaymentId(request.idempotencyKey()))
                .thenReturn(Optional.of(existingPayment.getId()));

        when(paymentRepository.findById(existingPayment.getId()))
                .thenReturn(Optional.of(existingPayment));

        when(paymentMapper.toPaymentResponse(existingPayment))
                .thenReturn(expectedResponse);

        PaymentResponse actualResponse = paymentService.createPayment(request);

        assertEquals(expectedResponse, actualResponse);

        verify(paymentRepository, never()).save(any(PaymentEntity.class));
        verify(paymentHistoryService, never()).recordStatusChange(any(), any(), any());
        verify(paymentEventService, never()).publishPaymentCreated(any());

    }


    private PaymentEntity createPaymentEntity(){
        PaymentEntity payment  = new PaymentEntity();
        payment.setId(UUID.randomUUID());
        payment.setCustomerId("cust-123");
        payment.setAmount(BigDecimal.valueOf(1500));
        payment.setCurrency("USD");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setIdempotencyKey("pay-req-001");
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());
        return payment;
    }
}
