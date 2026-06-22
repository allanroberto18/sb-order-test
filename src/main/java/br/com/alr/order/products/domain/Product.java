package br.com.alr.order.products.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record Product(UUID id, String name, BigDecimal price) {

  public Product {
    Objects.requireNonNull(id, "product id must not be null");
    Objects.requireNonNull(name, "product name must not be null");
    Objects.requireNonNull(price, "product price must not be null");
  }
}
