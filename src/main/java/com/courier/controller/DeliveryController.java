package com.courier.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courier.config.RabbitMQConfig;
import com.courier.dto.DeliveryRequest;
import com.courier.entity.DeliveryTask;
import com.courier.repository.DeliveryRepository;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Operation(summary = "Dispatch a Webhook", description = "Accepts a delivery request and processes it asynchronously")
    @PostMapping
    public ResponseEntity<String> createDelivery(@RequestBody DeliveryRequest request) {
        // 1. Save to DB (PENDING)
        DeliveryTask task = DeliveryTask.builder().clientUrl(request.url()).payload(request.payload())
                .status(DeliveryTask.TaskStatus.PENDING).attemptCount(0).build();

        task = repository.save(task);

        // 2. Push to Queue
        rabbitTemplate.convertAndSend(RabbitMQConfig.MAIN_EXCHANGE, RabbitMQConfig.MAIN_QUEUE, task.getId().toString());

        return ResponseEntity.accepted().body("Task Accepted. ID: " + task.getId());
    }
}