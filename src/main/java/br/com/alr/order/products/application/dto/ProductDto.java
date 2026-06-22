package br.com.alr.order.products.application.dto;

import br.com.alr.order.products.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(UUID id, String name, BigDecimal price) {

  public static ProductDto from(Product product) {
    return new ProductDto(
        product.id(),
        product.name(),
        product.price()
    );
  }
}
