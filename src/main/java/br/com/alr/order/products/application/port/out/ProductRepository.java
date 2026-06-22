package br.com.alr.order.products.application.port.out;

import br.com.alr.order.products.domain.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

  boolean existsById(UUID productId);

  Optional<Product> findById(UUID productId);

  List<Product> findAll();
}
