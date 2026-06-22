package br.com.alr.order.orderitems.domain;

import br.com.alr.order.orderitems.application.exception.InvalidOrderItemException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemTest {

  @Test
  void shouldCalculateTotalPrice() {
    OrderItem item = OrderItem.create(UUID.randomUUID(), "Notebook Pro 14", 3, new BigDecimal("10.50"));

    assertEquals(new BigDecimal("31.50"), item.totalPrice());
    assertEquals("Notebook Pro 14", item.getProductName());
  }

  @Test
  void shouldRejectBlankGetProductName() {
    assertThrows(InvalidOrderItemException.class,
        () -> OrderItem.create(UUID.randomUUID(), " ", 1, new BigDecimal("10.50")));
  }

  @Test
  void shouldRejectNonPositiveGetQuantity() {
    assertThrows(InvalidOrderItemException.class,
        () -> OrderItem.create(UUID.randomUUID(), "Notebook Pro 14", 0, new BigDecimal("10.50")));
  }

  @Test
  void shouldRejectNonPositiveGetUnitPrice() {
    assertThrows(InvalidOrderItemException.class,
        () -> OrderItem.create(UUID.randomUUID(), "Notebook Pro 14", 1, BigDecimal.ZERO));
  }
}
