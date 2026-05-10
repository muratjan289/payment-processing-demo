package com.murat.paymentdemo.application;


import com.murat.paymentdemo.domain.PaymentStatus;
import com.murat.paymentdemo.persistence.entity.PaymentHistoryEntity;
import com.murat.paymentdemo.persistence.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;


    public void recordStatusChange(
            UUID paymentId,
            PaymentStatus oldStatus,
            PaymentStatus newStatus
    ){
        PaymentHistoryEntity history = PaymentHistoryEntity.create(
                paymentId,
                oldStatus,
                newStatus
        );
        paymentHistoryRepository.save(history);
    }
    public List<PaymentHistoryEntity> getPaymentHistory(UUID paymentId){
        return paymentHistoryRepository.findByPaymentIdOrderByChangedAtAsc(paymentId);
    }
}
