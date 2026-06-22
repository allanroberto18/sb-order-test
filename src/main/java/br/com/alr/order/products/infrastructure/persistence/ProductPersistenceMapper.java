package br.com.alr.order.products.infrastructure.persistence;

import br.com.alr.order.products.domain.Product;

final class ProductPersistenceMapper {

  Product toDomain(ProductEntity entity) {
    return new Product(entity.getId(), entity.getName(), entity.getPrice());
  }
}
