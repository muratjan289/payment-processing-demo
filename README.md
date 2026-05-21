# Payment Processing Demo

A production-style backend demo application built with Java 17 and Spring Boot.

The project demonstrates a realistic payment processing flow with payment lifecycle management, idempotency, duplicate request protection, reliable state transitions, PostgreSQL persistence, Redis-based idempotency lookup, RabbitMQ event publishing, and clean REST API design.

## Tech Stack

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Redis
- RabbitMQ
- Liquibase
- Docker Compose
- Maven
- JUnit 5
- Mockito

## Project Goal

This project is not a simple CRUD application.

It is designed to demonstrate backend engineering patterns commonly used in payment, fintech, and integration-heavy systems:

- payment lifecycle management
- idempotent request processing
- duplicate payment protection
- state transition validation
- audit history
- asynchronous event publishing
- clean API contracts
- centralized exception handling
- production-style local infrastructure

## Architecture Overview

The application is implemented as a modular monolith with clear internal responsibilities.

Main logical layers:

- API layer — REST controllers and request/response DTOs
- Application layer — payment use cases and orchestration
- Domain layer — payment lifecycle and state transition rules
- Persistence layer — JPA entities and repositories
- Idempotency layer — Redis-based fast lookup with PostgreSQL fallback
- Messaging layer — RabbitMQ producer and consumer
- Exception layer — business exceptions and global error handling

High-level flow:

```text
Client
  ↓
PaymentController
  ↓
PaymentService
  ↓
PostgreSQL / Redis / RabbitMQ
```
## Payment Lifecycle

Supported payment statuses:

```text

PENDING

CONFIRMED

FAILED

CANCELLED

```

Allowed transitions:

```text

PENDING -> CONFIRMED

PENDING -> CANCELLED

PENDING -> FAILED

```

Invalid transitions:

```text

CONFIRMED -> CANCELLED

CANCELLED -> CONFIRMED

FAILED -> CONFIRMED

```

Invalid state transitions return:

```text

409 Conflict

```

## Idempotency Design

The service protects payment creation from duplicate requests using an idempotency key.

Request field example:

```json

{

  "idempotencyKey": "pay-req-001"

}

```

Redis is used as a fast lookup layer:

```text

payment:idempotency:{idempotencyKey} -> paymentId

```

The Redis key has a TTL of 24 hours.

PostgreSQL is still used as the final consistency guarantee through a unique constraint on:

```text

payments.idempotency_key

```

This means Redis improves performance, while PostgreSQL protects data consistency even if Redis is unavailable, expired, or cleared.

## Messaging with RabbitMQ

The application publishes events after important payment state changes.

Published events:

- PaymentCreatedEvent

- PaymentConfirmedEvent

- PaymentCancelledEvent

RabbitMQ configuration:

```text

Exchange: payment.exchange

Queue: payment.events.queue

Routing keys:

payment.created

payment.confirmed

payment.cancelled

```

A consumer is included in the project and logs received payment events. It can be extended later for notifications, analytics, audit processing, or integration with external systems.

## Database Model

### payments

Stores the current state of a payment.

Main fields:

- id

- customer_id

- amount

- currency

- status

- idempotency_key

- created_at

- updated_at

### payment_history

Stores an append-only history of payment status changes.

Main fields:

- id

- payment_id

- old_status

- new_status

- changed_at

## REST API

### Create payment

```http

POST /api/payments

```

Request:

```json

{

  "customerId": "cust-123",

  "amount": 1500,

  "currency": "USD",

  "idempotencyKey": "pay-req-001"

}

```

Response:

```json

{

  "paymentId": "uuid",

  "customerId": "cust-123",

  "amount": 1500,

  "currency": "USD",

  "status": "PENDING",

  "createdAt": "2026-05-01T10:00:00Z",

  "updatedAt": "2026-05-01T10:00:00Z"

}

```

### Get payment

```http

GET /api/payments/{paymentId}

```

### Confirm payment

```http

POST /api/payments/{paymentId}/confirm

```

Response:

```json

{

  "paymentId": "uuid",

  "status": "CONFIRMED"

}

```

### Cancel payment

```http

POST /api/payments/{paymentId}/cancel

```

Response:

```json

{

  "paymentId": "uuid",

  "status": "CANCELLED"

}

```

### Get payment history

```http

GET /api/payments/{paymentId}/history

```

Response:

```json

[

  {

    "oldStatus": null,

    "newStatus": "PENDING",

    "changedAt": "2026-05-01T10:00:00Z"

  },

  {

    "oldStatus": "PENDING",

    "newStatus": "CONFIRMED",

    "changedAt": "2026-05-01T10:05:00Z"

  }

]

```

## Error Handling

The application uses centralized exception handling with `@RestControllerAdvice`.

Examples:

| Scenario | HTTP Status |

|---|---|

| Payment not found | 404 Not Found |

| Invalid state transition | 409 Conflict |

| Request validation failed | 400 Bad Request |

| Unexpected error | 500 Internal Server Error |

Example error response:

```json

{

  "timestamp": "2026-05-01T10:00:00Z",

  "status": 409,

  "error": "Conflict",

  "message": "Invalid payment state transition: CONFIRMED -> CANCELLED",

  "path": "/api/payments/{id}/cancel",

  "details": []

}

```

## Local Run

### 1. Start infrastructure

```bash

docker compose up -d

```

This starts:

- PostgreSQL on port `5432`

- Redis on port `6379`

- RabbitMQ on port `5672`

- RabbitMQ Management UI on port `15672`

RabbitMQ UI:

```text

http://localhost:15672

```

Credentials:

```text

payment_user / payment_password

```

### 2. Run application

```bash

./mvnw spring-boot:run

```

Application starts on:

```text

http://localhost:8080

```

### 3. Run tests

```bash

./mvnw test

```

## Example cURL Requests

### Create payment

```bash

curl -X POST http://localhost:8080/api/payments \

  -H "Content-Type: application/json" \

  -d '{

    "customerId": "cust-123",

    "amount": 1500,

    "currency": "USD",

    "idempotencyKey": "pay-req-001"

  }'

```

### Get payment

```bash

curl http://localhost:8080/api/payments/{paymentId}

```

### Confirm payment

```bash

curl -X POST http://localhost:8080/api/payments/{paymentId}/confirm

```

### Cancel payment

```bash

curl -X POST http://localhost:8080/api/payments/{paymentId}/cancel

```

### Get payment history

```bash

curl http://localhost:8080/api/payments/{paymentId}/history

```

## Why PostgreSQL

PostgreSQL is used as the source of truth because payment data requires strong consistency, durable storage, transactional guarantees, and reliable constraints.

The unique constraint on `idempotency_key` protects the system from duplicate payment creation at the database level.

## Why Redis

Redis is used for fast idempotency lookup.

Instead of checking PostgreSQL first for every repeated request, the service can quickly resolve:

```text

idempotencyKey -> paymentId

```

Redis improves performance, while PostgreSQL remains the final data consistency layer.

## Why RabbitMQ

RabbitMQ is used to demonstrate decoupled event-driven processing.

Payment state changes publish events that can be consumed independently by other parts of the system. This keeps the payment flow extensible without tightly coupling future processing to the core payment service.

## Tests

Current test coverage includes:

- payment state transition validation

- payment creation flow

- idempotency behavior at service level

## Future Improvements

Possible next improvements:

- transactional outbox pattern for reliable event publishing

- retry/backoff for RabbitMQ publishing

- dead-letter queue handling

- integration tests with Testcontainers

- Spring Security / API key authentication

- Actuator metrics

- Prometheus + Grafana monitoring

- payment provider mock

- refund flow

- webhook simulation