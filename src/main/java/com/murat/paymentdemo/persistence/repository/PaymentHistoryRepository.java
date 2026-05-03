package com.murat.paymentdemo.persistence.repository;

import com.murat.paymentdemo.persistence.entity.PaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentHistoryRepository  extends JpaRepository<PaymentHistoryEntity, UUID> {

    List<PaymentHistoryEntity> findByPaymentIdOrderByChangedAtAsc(UUID paymentId);
}
