# CourierConnect

**Reliable, asynchronous webhook delivery service.**

CourierConnect acts as middleware to ensure webhooks are delivered even if the receiving service is temporarily down. It decouples request ingestion from the delivery process using a message queue, preventing data loss during traffic spikes.

---

## 🏗 System Architecture

The system follows an **Event-Driven Architecture** to ensure non-blocking operations.

### Data Flow

1. **Ingest**
   API receives a delivery request and persists it to **PostgreSQL**
   *(Status: `PENDING`)*

2. **Queue**
   The request ID is published to **RabbitMQ** for asynchronous processing

3. **Process**
   A worker consumes the message and attempts delivery to the target URL

4. **Update**

   * **Success:** Update DB status to `DELIVERED`
   * **Failure:** Update DB status to `FAILED` (supports future retries)

---

## 🛠 Tech Stack

* **Core:** Java 17, Spring Boot
* **Messaging:** RabbitMQ (AMQP)
* **Database:** PostgreSQL
* **Migration:** Flyway (database schema versioning)
* **Containerization:** Docker & Docker Compose
* **Documentation:** Swagger UI (OpenAPI)

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

## 💡 Key Design Decisions

* **Why RabbitMQ?**
  RabbitMQ was chosen over Kafka due to its simpler setup and strong support for job-queue patterns where guaranteed delivery is more important than strict ordering.

* **Flyway**
  Ensures database schema consistency across local, Docker, and future production environments.

* **Transactional Messaging**
  Database persistence and queue publishing are coordinated to avoid message loss when the queue is temporarily unavailable.

---

## 👤 Author

**Daksh Arora**


---

