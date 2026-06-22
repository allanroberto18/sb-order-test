package br.com.alr.order.orders.application.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException(UUID productId) {
    super("product not found: " + productId);
  }
}
