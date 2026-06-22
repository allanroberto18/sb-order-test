package br.com.alr.order.products.infrastructure.persistence;

import br.com.alr.order.products.domain.Product;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class ProductRepository implements br.com.alr.order.products.application.port.out.ProductRepository {

  private final ProductJpaRepository productJpaRepository;
  private final ProductPersistenceMapper mapper = new ProductPersistenceMapper();

  public ProductRepository(ProductJpaRepository productJpaRepository) {
    this.productJpaRepository = productJpaRepository;
  }

  @Override
  public boolean existsById(UUID productId) {
    return productJpaRepository.existsById(productId);
  }

  @Override
  public Optional<Product> findById(UUID productId) {
    return productJpaRepository.findById(productId)
        .map(mapper::toDomain);
  }

  @Override
  public List<Product> findAll() {
    return productJpaRepository.findAll().stream()
        .map(mapper::toDomain)
        .toList();
  }
}
