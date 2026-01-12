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

### 🏗️ Tech Stack
* **Language:** Java 17
* **Framework:** Spring Boot
* **Messaging:** RabbitMQ (Exchanges, Queues, DLQ)
* **Database:** [PostgreSQL / MySQL] with Spring Data JPA
* **Build:** Maven
