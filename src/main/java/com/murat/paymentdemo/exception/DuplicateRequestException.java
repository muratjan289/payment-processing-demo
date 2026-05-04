package com.murat.paymentdemo.exception;

public class DuplicateRequestException extends RuntimeException {
    public DuplicateRequestException(String idempotencyKey){
        super("Duplicate request detected for idempotencyKey " + idempotencyKey);
    }
}
