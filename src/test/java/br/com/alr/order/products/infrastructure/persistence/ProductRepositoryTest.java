package br.com.alr.order.products.infrastructure.persistence;

import br.com.alr.order.products.domain.Product;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ProductRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private EntityManager entityManager;

  @Test
  void shouldFindProductByIdAndMapToDomain() {
    ProductEntity entity = ProductEntity.builder()
        .id(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
        .name("Notebook Pro 14")
        .price(new BigDecimal("7499.90"))
        .build();
    entityManager.persist(entity);
    entityManager.flush();
    entityManager.clear();

    Optional<Product> result = productRepository.findById(entity.getId());

    assertThat(result).isPresent();
    assertThat(result.orElseThrow().id()).isEqualTo(entity.getId());
    assertThat(result.orElseThrow().name()).isEqualTo("Notebook Pro 14");
    assertThat(result.orElseThrow().price()).isEqualByComparingTo("7499.90");
  }

  @Test
  void shouldReturnExistenceAndAllProducts() {
    ProductEntity notebook = ProductEntity.builder()
        .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
        .name("Notebook Pro 14")
        .price(new BigDecimal("7499.90"))
        .build();
    ProductEntity mouse = ProductEntity.builder()
        .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
        .name("Wireless Mouse")
        .price(new BigDecimal("199.90"))
        .build();
    entityManager.persist(notebook);
    entityManager.persist(mouse);
    entityManager.flush();
    entityManager.clear();

    boolean existing = productRepository.existsById(notebook.getId());
    boolean missing = productRepository.existsById(UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"));
    List<Product> products = productRepository.findAll();

    assertThat(existing).isTrue();
    assertThat(missing).isFalse();
    assertThat(products)
        .extracting(Product::id)
        .containsExactlyInAnyOrder(notebook.getId(), mouse.getId());
  }
}
