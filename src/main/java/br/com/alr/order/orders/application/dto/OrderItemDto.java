package br.com.alr.order.orders.application.dto;

import br.com.alr.order.orderitems.domain.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(
    UUID id,
    UUID productId,
    String productName,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice
) {

  public static OrderItemDto from(OrderItem item) {
    return new OrderItemDto(
        item.getId(),
        item.getProductId(),
        item.getProductName(),
        item.getQuantity(),
        item.getUnitPrice(),
        item.totalPrice());
  }
}
