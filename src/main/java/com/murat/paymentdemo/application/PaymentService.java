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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {



    private final PaymentRepository paymentRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final PaymentMapper paymentMapper;
    private final IdempotencyService idempotencyService;
    private final PaymentEventService paymentEventService;


    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request){
        Optional<UUID> existingPaymentId = idempotencyService.findPaymentId(request.idempotencyKey());
        if(existingPaymentId.isPresent()){
            PaymentEntity payment = findPaymentOrThrow(existingPaymentId.get());
            return paymentMapper.toPaymentResponse(payment);

        }

        Optional<PaymentEntity> existingPayment  =paymentRepository.findByIdempotencyKey(request.idempotencyKey());

        if(existingPayment.isPresent()){
            PaymentEntity payment = existingPayment.get();
            idempotencyService.savePaymentId(request.idempotencyKey(), payment.getId());
            return paymentMapper.toPaymentResponse(payment);
        }

        return  createNewPayment(request);
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
       paymentEventService.publishPaymentConfirmed(savedPayment);

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

       paymentEventService.publishPaymentDeleted(savedPayment);
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
        idempotencyService.savePaymentId(
                request.idempotencyKey(),
                savedPayment.getId()
        );
        paymentEventService.publishPaymentCreated(savedPayment);
        return paymentMapper.toPaymentResponse(savedPayment);
    }

    private PaymentEntity findPaymentOrThrow(UUID paymentId){
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }
}
