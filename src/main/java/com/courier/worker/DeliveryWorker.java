package com.courier.worker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.courier.config.RabbitMQConfig;
import com.courier.entity.DeliveryTask;
import com.courier.repository.DeliveryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryWorker {

    private final DeliveryRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final WebClient webClient = WebClient.create();

    @RabbitListener(queues = RabbitMQConfig.MAIN_QUEUE)
    public void processDelivery(String taskId) {
        UUID id = UUID.fromString(taskId);
        DeliveryTask task = repository.findById(id).orElse(null);

        if (task == null || task.getStatus() == DeliveryTask.TaskStatus.COMPLETED)
            return;

        try {
            log.info("Attempting delivery for Task: {} (Attempt {})", id, task.getAttemptCount() + 1);

            // 1. HTTP Request to Client
            String response = webClient.post()
                    .uri(task.getClientUrl())
                    .header("Content-Type", "application/json")
                    .bodyValue(task.getPayload())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 2. Success!
            task.setStatus(DeliveryTask.TaskStatus.COMPLETED);
            task.setLastErrorMessage("Success: " + response);
            repository.save(task);
            log.info("Task {} DELIVERED successfully.", id);

        } catch (Exception e) {
            // 3. Failure!
            handleFailure(task, e.getMessage());
        }
    }

    private void handleFailure(DeliveryTask task, String error) {
        int attempts = task.getAttemptCount() + 1;
        task.setAttemptCount(attempts);

        if (error != null && error.length() > 500) {
            error = error.substring(0, 500); // Truncate to fit database
        }
        
        task.setLastErrorMessage(error);

        if (attempts >= 5) { // Max Retries
            task.setStatus(DeliveryTask.TaskStatus.FAILED);
            log.error("Task {} FAILED completely after {} attempts.", task.getId(), attempts);
        } else {
            task.setStatus(DeliveryTask.TaskStatus.RETRYING);

            // EXPONENTIAL BACKOFF LOGIC
            // 2^1 = 2s, 2^2 = 4s, 2^3 = 8s
            long delayMs = (long) Math.pow(2, attempts) * 1000;
            task.setNextRetryAt(LocalDateTime.now().plus(Duration.ofMillis(delayMs)));

            log.warn("Task {} failed. Retrying in {} ms", task.getId(), delayMs);

            // Send to WAIT QUEUE with TTL (Time To Live)
            rabbitTemplate.convertAndSend(RabbitMQConfig.WAIT_EXCHANGE, RabbitMQConfig.WAIT_QUEUE,
                    task.getId().toString(), message -> {
                        message.getMessageProperties().setExpiration(String.valueOf(delayMs));
                        return message;
                    });
        }
        repository.save(task);
    }

}
