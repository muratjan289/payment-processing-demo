package com.murat.paymentdemo.persistence.entity;


import com.murat.paymentdemo.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_history")
public class PaymentHistoryEntity {

    @Id
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 30)
    private PaymentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private PaymentStatus newStatus;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;
    public static PaymentHistoryEntity create(
            UUID paymentId,
            PaymentStatus oldStatus,
            PaymentStatus newStatus

    ) {

        PaymentHistoryEntity history = new PaymentHistoryEntity();

        history.setId(UUID.randomUUID());
        history.setPaymentId(paymentId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedAt(Instant.now());

        return history;

    }

}
