package br.com.alr.order.orders.application.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

  public OrderNotFoundException(UUID orderId) {
    super("order not found: " + orderId);
  }
}
