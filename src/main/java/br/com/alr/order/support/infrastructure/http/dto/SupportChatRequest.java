package br.com.alr.order.support.infrastructure.http.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record SupportChatRequest(
    @NotBlank(message = "message must not be blank")
    String message,
    UUID orderId
) { }
