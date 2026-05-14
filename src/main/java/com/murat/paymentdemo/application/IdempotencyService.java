package com.murat.paymentdemo.application;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotencyService{

private static final String IDEMPOTENCY_KEY_PREFIX = "payment:idempotency:";
private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);

private final StringRedisTemplate redisTemplate;

    public Optional<UUID>  findPaymentId(String idempotencyKey) {
    String redisKey = buildRedisKey(idempotencyKey);
        String paymentId = redisTemplate.opsForValue().get(redisKey);

        if (paymentId == null) {
            return Optional.empty();
        }
    return Optional.of(UUID.fromString(paymentId));
    }

    public void savePaymentId(String idempotencyKey, UUID paymentId) {
        String redisKey = buildRedisKey(idempotencyKey);

        redisTemplate.opsForValue().set(
                redisKey,
                paymentId.toString(),
                IDEMPOTENCY_TTL
        );
    }

private String buildRedisKey(String idempotencyKey) {
    return IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
}

}
