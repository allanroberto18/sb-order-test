package br.com.alr.order.orders.application.dto;

import br.com.alr.order.orders.domain.Order;
import br.com.alr.order.orders.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDto(
    UUID id,
    OrderStatus status,
    BigDecimal totalAmount,
    Instant createdAt,
    Instant updatedAt,
    List<OrderItemDto> items
) {

  public static OrderDto from(Order order) {
    return new OrderDto(
        order.getId(),
        order.getStatus(),
        order.totalAmount(),
        order.getCreatedAt(),
        order.getUpdatedAt(),
        order.getItems().stream().map(OrderItemDto::from).toList());
  }
}
