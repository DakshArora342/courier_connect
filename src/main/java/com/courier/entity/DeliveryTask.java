package com.courier.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "delivery_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String clientUrl; // Where to send the webhook

    @Column(columnDefinition = "TEXT")
    private String payload; // The JSON data

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private int attemptCount;

    private LocalDateTime nextRetryAt;

    private String lastErrorMessage;

    public enum TaskStatus {
        PENDING, COMPLETED, FAILED, RETRYING
    }
}