package br.com.alr.order.orders.domain;

import br.com.alr.order.orderitems.domain.OrderItem;
import br.com.alr.order.orders.application.exception.InvalidOrderException;
import br.com.alr.order.orders.application.exception.OrderCancellationNotAllowedException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public final class Order {

  private final UUID id;
  private final List<OrderItem> items;
  private final Instant createdAt;
  private OrderStatus status;
  private Instant updatedAt;

  private Order(
      UUID id, List<OrderItem> items,
      OrderStatus status,
      Instant createdAt,
      Instant updatedAt
  ) throws InvalidOrderException {
    this.id = Objects.requireNonNull(id, "order id must not be null");
    this.items = List.copyOf(requireItems(items));
    this.status = Objects.requireNonNull(status, "status must not be null");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
  }

  public static Order create(List<OrderItem> items, Clock clock) throws InvalidOrderException {
    Instant now = Instant.now(requireClock(clock));
    return new Order(UUID.randomUUID(), items, OrderStatus.PENDING, now, now);
  }

  public static Order restore(
      UUID id, List<OrderItem> items,
      OrderStatus status,
      Instant createdAt,
      Instant updatedAt
  ) throws InvalidOrderException {
    return new Order(id, items, status, createdAt, updatedAt);
  }

  private static List<OrderItem> requireItems(List<OrderItem> items) throws InvalidOrderException {
    if (items == null || items.isEmpty()) {
      throw new InvalidOrderException("order must contain at least one item");
    }

    return items;
  }

  private static Clock requireClock(Clock clock) {
    return Objects.requireNonNull(clock, "clock must not be null");
  }

  public BigDecimal totalAmount() {
    return items.stream()
        .map(OrderItem::totalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void cancel(Clock clock) throws OrderCancellationNotAllowedException {
    if (status != OrderStatus.PENDING) {
      throw new OrderCancellationNotAllowedException(status);
    }

    transitionTo(OrderStatus.CANCELLED, clock);
  }

  public void markProcessing(Clock clock) {
    if (status == OrderStatus.PENDING) {
      transitionTo(OrderStatus.PROCESSING, clock);
    }
  }

  private void transitionTo(OrderStatus nextStatus, Clock clock) {
    this.status = Objects.requireNonNull(nextStatus, "nextStatus must not be null");
    this.updatedAt = Instant.now(requireClock(clock));
  }
}
