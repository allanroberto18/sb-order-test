package br.com.alr.order.orders.application.dto;

import br.com.alr.order.orders.domain.Order;
import br.com.alr.order.orders.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderSummaryDto(
    UUID id,
    OrderStatus status,
    BigDecimal totalAmount,
    Instant createdAt,
    Instant updatedAt
) {

  public static OrderSummaryDto from(Order order) {
    return new OrderSummaryDto(
        order.getId(),
        order.getStatus(),
        order.totalAmount(),
        order.getCreatedAt(),
        order.getUpdatedAt());
  }
}
