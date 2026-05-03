package com.murat.paymentdemo.persistence.entity;


import com.murat.paymentdemo.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payments_idempotency_key",
                        columnNames = "idempotency_key"
                )
        }
)
public class PaymentEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false, length = 100)
    private String customerId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "idempotency_key", nullable = false, length = 150)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)

    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)

    private Instant updatedAt;

    public static PaymentEntity create(
            String customerId,
            BigDecimal amount,
            String currency,
            String idempotencyKey
    ){
        Instant now = Instant.now();

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setId(UUID.randomUUID());
        paymentEntity.setCustomerId(customerId);
        paymentEntity.setAmount(amount);
        paymentEntity.setCurrency(currency);
        paymentEntity.setStatus(PaymentStatus.PENDING);
        paymentEntity.setIdempotencyKey(idempotencyKey);
        paymentEntity.setCreatedAt(now);
        paymentEntity.setUpdatedAt(now);

        return paymentEntity;

    }

    public void changeStatus(PaymentStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }



}
