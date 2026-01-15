---

# Courier Connect 📨

A robust, fault-tolerant webhook delivery service built with **Spring Boot** and **RabbitMQ**.

### 💡 The Problem
In distributed systems, sending data between services is risky. Destination servers might be down, experience latency, or reject requests (e.g., `415 Unsupported Media Type` or `503 Service Unavailable`). Simple synchronous HTTP calls result in data loss during these failures.

### 🛠️ The Solution
Courier Connect decouples the ingestion of tasks from their delivery. It ensures **At-Least-Once Delivery** reliability through an event-driven architecture.

**Key Features:**
* **Asynchronous Processing:** Uses RabbitMQ to buffer delivery tasks, ensuring the ingestion API remains fast and non-blocking.
* **Smart Retries (Exponential Backoff):** If a delivery fails, the system waits increasingly longer intervals (2s, 4s, 8s, 16s...) before retrying.
* **Dead Letter Queue (DLQ):** Messages that fail after maximum attempts are not deleted; they are moved to a DLQ for manual inspection and debugging.
* **Reactive Client:** Utilizes `Spring WebClient` for efficient, non-blocking HTTP requests.

![Courier Connect webhook delivery architecture with RabbitMQ, DLQ, and retry mechanism](https://i.ibb.co/GQJs6wDy/Screenshot-2026-01-15-203739.png)

### 🏗️ Tech Stack
* **Language:** Java 17
* **Framework:** Spring Boot
* **Messaging:** RabbitMQ (Exchanges, Queues, DLQ)
* **Database:** PostgreSQL with Spring Data JPA
* **Build:** Maven

---

## 📸 Proof of Resilience (Chaos Engineering)

To prove the system’s fault tolerance, a **ChaosController** was implemented that intentionally fails **70% of delivery attempts**. This simulates real-world scenarios such as unstable downstream services or intermittent network failures.

The logs below demonstrate the **Exponential Backoff retry mechanism** in action.

**Retry intervals:**
`2s → 4s → 8s → 16s`

```
INFO [Worker] - Attempt 1 failed. Retrying in 2 seconds...
INFO [Worker] - Attempt 2 failed. Retrying in 4 seconds...
INFO [Worker] - Attempt 3 failed. Retrying in 8 seconds...
INFO [Worker] - Attempt 4 success! Delivery confirmed.
```

This confirms that:

* The ingestion API remains responsive
* Failed deliveries are retried automatically
* The system recovers gracefully once the downstream service becomes available
* Messages exceeding retry limits are routed to the **Dead Letter Queue (DLQ)**

---

## 🚀 Quick Start

### Option 1: Docker (Recommended)

Run the entire infrastructure (Application + Database + Queue):

```bash
docker-compose up --build
```

* **Swagger Docs:** `http://localhost:8080/swagger-ui.html`

---

### Option 2: Local Development

Run the Java application locally while keeping infrastructure in Docker.

#### 1. Start Infrastructure

```bash
docker-compose up postgres rabbitmq
```

#### 2. Run Application

```bash
mvnw spring-boot:run
```

---

## 🔌 API Usage

### Send a Delivery Request

> **Note:** The `payload` must be a **stringified JSON object**.

**POST** `/api/deliveries`

```json
{
  "url": "https://webhook.site/destination",
  "payload": "{\"name\":\"test\",\"salary\":\"123\",\"age\":\"23\"}"
}
```

---
