package br.com.alr.order.orders.domain;

import br.com.alr.order.orderitems.domain.OrderItem;
import br.com.alr.order.orders.application.exception.InvalidOrderException;
import br.com.alr.order.orders.application.exception.OrderCancellationNotAllowedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

  private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-06-20T20:00:00Z"), ZoneOffset.UTC);

  @Test
  void shouldCreateOrderAsPending() {
    Order order = Order.create(List.of(OrderItem.create(UUID.randomUUID(), "Notebook Pro 14", 2, new BigDecimal("12.50"))), FIXED_CLOCK);

    assertEquals(OrderStatus.PENDING, order.getStatus());
    assertEquals(new BigDecimal("25.00"), order.totalAmount());
    assertEquals(Instant.parse("2026-06-20T20:00:00Z"), order.getCreatedAt());
    assertEquals(order.getCreatedAt(), order.getUpdatedAt());
  }

  @Test
  void shouldCancelPendingOrder() {
    Order order = Order.create(List.of(OrderItem.create(UUID.randomUUID(), "Notebook Pro 14", 1, new BigDecimal("12.50"))), FIXED_CLOCK);
    Clock nextClock = Clock.fixed(Instant.parse("2026-06-20T20:01:00Z"), ZoneOffset.UTC);

    order.cancel(nextClock);

    assertEquals(OrderStatus.CANCELLED, order.getStatus());
    assertEquals(Instant.parse("2026-06-20T20:01:00Z"), order.getUpdatedAt());
  }

  @Test
  void shouldRejectCancellationWhenOrderIsNotPending() {
    Order order = Order.restore(
        UUID.randomUUID(),
        List.of(OrderItem.create(UUID.randomUUID(), "Notebook Pro 14", 1, new BigDecimal("12.50"))),
        OrderStatus.PROCESSING,
        Instant.parse("2026-06-20T20:00:00Z"),
        Instant.parse("2026-06-20T20:00:00Z"));

    assertThrows(OrderCancellationNotAllowedException.class, () -> order.cancel(FIXED_CLOCK));
  }

  @Test
  void shouldRejectOrderWithoutGetItems() {
    assertThrows(InvalidOrderException.class, () -> Order.create(List.of(), FIXED_CLOCK));
  }
}
