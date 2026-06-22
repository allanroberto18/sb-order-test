package br.com.alr.order.orders.infrastructure.http.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderItemRequest(
    @NotNull UUID productId,
    @Positive int quantity,
    @NotNull @DecimalMin(value = "0.01") BigDecimal unitPrice) {
}
