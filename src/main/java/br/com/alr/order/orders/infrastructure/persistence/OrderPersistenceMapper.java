package br.com.alr.order.orders.infrastructure.persistence;

import br.com.alr.order.orderitems.domain.OrderItem;
import br.com.alr.order.orderitems.infrastructure.persistence.OrderItemEntity;
import br.com.alr.order.orders.application.dto.OrderSummaryDto;
import br.com.alr.order.orders.domain.Order;

import java.util.List;

final class OrderPersistenceMapper {

  OrderEntity toEntity(Order order) {
    OrderEntity entity = OrderEntity.builder()
        .id(order.getId())
        .status(order.getStatus())
        .totalAmount(order.totalAmount())
        .createdAt(order.getCreatedAt())
        .updatedAt(order.getUpdatedAt())
        .build();

    List<OrderItemEntity> itemEntities = order.getItems().stream()
        .map(item -> toEntity(item, entity))
        .toList();
    entity.replaceItems(itemEntities);
    return entity;
  }

  Order toDomain(OrderEntity entity) {
    List<OrderItem> items = entity.getItems().stream()
        .map(item -> OrderItem.restore(
            item.getId(),
            item.getProductId(),
            item.getProduct().getName(),
            item.getQuantity(),
            item.getUnitPrice()))
        .toList();
    return Order.restore(entity.getId(), items, entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
  }

  OrderSummaryDto toSummary(OrderEntity entity) {
    return new OrderSummaryDto(
        entity.getId(),
        entity.getStatus(),
        entity.getTotalAmount(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  private OrderItemEntity toEntity(OrderItem item, OrderEntity order) {
    return OrderItemEntity.builder()
        .id(item.getId())
        .productId(item.getProductId())
        .quantity(item.getQuantity())
        .unitPrice(item.getUnitPrice())
        .order(order)
        .build();
  }
}
