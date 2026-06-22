package br.com.alr.order.orders.application.exception;

import br.com.alr.order.orders.domain.OrderStatus;

public class OrderCancellationNotAllowedException extends RuntimeException {

  public OrderCancellationNotAllowedException(OrderStatus status) {
    super("order cannot be cancelled when status is " + status);
  }
}
