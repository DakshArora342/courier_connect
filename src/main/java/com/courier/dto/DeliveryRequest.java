package com.courier.dto;

import org.hibernate.validator.constraints.URL;
import jakarta.validation.constraints.NotBlank;

public record DeliveryRequest(
        @NotBlank(message = "URL is required")
        @URL(message = "Must be a valid URL")
        String url,

        @NotBlank(message = "Payload cannot be empty")
        String payload
) {}
