package com.murat.paymentdemo.api.mapper;

import com.murat.paymentdemo.api.dto.PaymentHistoryResponse;
import com.murat.paymentdemo.api.dto.PaymentResponse;
import com.murat.paymentdemo.api.dto.PaymentStatusResponse;
import com.murat.paymentdemo.persistence.entity.PaymentEntity;
import com.murat.paymentdemo.persistence.entity.PaymentHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toPaymentResponse(PaymentEntity payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    public PaymentStatusResponse toPaymentStatusResponse(PaymentEntity payment) {
        return new PaymentStatusResponse(
                payment.getId(),
                payment.getStatus()
        );
    }

    public PaymentHistoryResponse toPaymentHistoryResponse(PaymentHistoryEntity history){
        return new PaymentHistoryResponse(
                history.getOldStatus(),
                history.getNewStatus(),
                history.getChangedAt()
        );
    }
}
