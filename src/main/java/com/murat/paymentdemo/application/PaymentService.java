package com.murat.paymentdemo.application;


import com.murat.paymentdemo.api.dto.CreatePaymentRequest;
import com.murat.paymentdemo.api.dto.PaymentHistoryResponse;
import com.murat.paymentdemo.api.dto.PaymentResponse;
import com.murat.paymentdemo.api.dto.PaymentStatusResponse;
import com.murat.paymentdemo.api.mapper.PaymentMapper;
import com.murat.paymentdemo.domain.PaymentStateMachine;
import com.murat.paymentdemo.domain.PaymentStatus;
import com.murat.paymentdemo.exception.PaymentNotFoundException;
import com.murat.paymentdemo.persistence.entity.PaymentEntity;
import com.murat.paymentdemo.persistence.entity.PaymentHistoryEntity;
import com.murat.paymentdemo.persistence.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {



    private final PaymentRepository paymentRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request){
        return  paymentRepository.findByIdempotencyKey(request.idempotencyKey())
                .map(paymentMapper::toPaymentResponse)
                .orElseGet(() -> createNewPayment(request));
    }

    @Transactional(readOnly = true)
   public PaymentStatusResponse getPayment(UUID paymentId){
        PaymentEntity payment = findPaymentOrThrow(paymentId);
        return paymentMapper.toPaymentStatusResponse(payment);

   }

   @Transactional
   public PaymentStatusResponse confirmPayment(UUID paymentId){

        PaymentEntity payment = findPaymentOrThrow(paymentId);

       PaymentStatus oldStatus = payment.getStatus();
       PaymentStateMachine.validateTransition(oldStatus,PaymentStatus.CONFIRMED);

       payment.changeStatus(PaymentStatus.CONFIRMED);
       PaymentEntity savedPayment = paymentRepository.save(payment);

       paymentHistoryService.recordStatusChange(
               savedPayment.getId(),
               oldStatus,
               savedPayment.getStatus()
       );

        return paymentMapper.toPaymentStatusResponse(savedPayment);
   }

   @Transactional
   public PaymentStatusResponse cancelPayment(UUID paymentId){

        PaymentEntity payment = findPaymentOrThrow(paymentId);

       PaymentStatus oldStatus = payment.getStatus();
       PaymentStateMachine.validateTransition(oldStatus,PaymentStatus.CANCELLED);

       payment.changeStatus(PaymentStatus.CANCELLED);
       PaymentEntity savedPayment = paymentRepository.save(payment);

       paymentHistoryService.recordStatusChange(
               savedPayment.getId(),
               oldStatus,
               savedPayment.getStatus()
       );
       return paymentMapper.toPaymentStatusResponse(savedPayment);
   }

   @Transactional(readOnly = true)
   public List<PaymentHistoryResponse> getPaymentHistory(UUID paymentId){

        findPaymentOrThrow(paymentId);

        List<PaymentHistoryEntity> history = paymentHistoryService.getPaymentHistory(paymentId);

        return history.stream()
                .map(paymentMapper::toPaymentHistoryResponse)
                .toList();


   }




    private PaymentResponse createNewPayment(CreatePaymentRequest request){
        PaymentEntity payment = PaymentEntity.create(
                request.customerId(),
                request.amount(),
                request.currency(),
                request.idempotencyKey()
        );

        PaymentEntity savedPayment = paymentRepository.save(payment);

        paymentHistoryService.recordStatusChange(
                savedPayment.getId(),
                null,
                savedPayment.getStatus()
        );
        return paymentMapper.toPaymentResponse(savedPayment);
    }

    private PaymentEntity findPaymentOrThrow(UUID paymentId){
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }
}
