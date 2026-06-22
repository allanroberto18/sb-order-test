package br.com.alr.order.orders.infrastructure.persistence;

import br.com.alr.order.orderitems.domain.OrderItem;
import br.com.alr.order.orders.application.dto.OrderSummaryDto;
import br.com.alr.order.orders.application.port.out.OrderPage;
import br.com.alr.order.orders.domain.Order;
import br.com.alr.order.orders.domain.OrderStatus;
import br.com.alr.order.products.infrastructure.persistence.ProductEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(OrderRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrderRepositoryTest {

  private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-06-22T10:15:30Z"), ZoneOffset.UTC);

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private EntityManager entityManager;

  @Test
  void shouldSaveAndLoadDetailedOrderWithItemsAndProductName() {
    ProductEntity product = persistProduct(
        UUID.fromString("11111111-1111-1111-1111-111111111111"),
        "Notebook Pro 14",
        "7499.90");
    OrderItem item = OrderItem.create(product.getId(), product.getName(), 2, new BigDecimal("7499.90"));
    Order order = Order.create(List.of(item), FIXED_CLOCK);

    orderRepository.save(order);
    entityManager.flush();
    entityManager.clear();

    Optional<Order> result = orderRepository.findDetailedById(order.getId());

    assertThat(result).isPresent();
    Order restored = result.orElseThrow();
    assertThat(restored.getId()).isEqualTo(order.getId());
    assertThat(restored.getStatus()).isEqualTo(OrderStatus.PENDING);
    assertThat(restored.totalAmount()).isEqualByComparingTo("14999.80");
    assertThat(restored.getItems()).hasSize(1);
    assertThat(restored.getItems().getFirst().getProductId()).isEqualTo(product.getId());
    assertThat(restored.getItems().getFirst().getProductName()).isEqualTo("Notebook Pro 14");
  }

  @Test
  void shouldFindPageWithStatusFilterAndNormalizedArguments() {
    ProductEntity product = persistProduct(
        UUID.fromString("22222222-2222-2222-2222-222222222222"),
        "Wireless Mouse",
        "199.90");
    orderRepository.save(Order.create(List.of(OrderItem.create(product.getId(), product.getName(), 1, new BigDecimal("199.90"))), FIXED_CLOCK));
    Order processingOrder = Order.create(List.of(OrderItem.create(product.getId(), product.getName(), 3, new BigDecimal("199.90"))), FIXED_CLOCK);
    processingOrder.markProcessing(FIXED_CLOCK);
    orderRepository.save(processingOrder);
    entityManager.flush();
    entityManager.clear();

    OrderPage<OrderSummaryDto> page = orderRepository.findPage(OrderStatus.PROCESSING, -4, 999);

    assertThat(page.page()).isZero();
    assertThat(page.size()).isEqualTo(100);
    assertThat(page.totalElements()).isEqualTo(1);
    assertThat(page.totalPages()).isEqualTo(1);
    assertThat(page.content()).extracting(OrderSummaryDto::status).containsExactly(OrderStatus.PROCESSING);
  }

  @Test
  void shouldTransitionOnlyPendingOrdersToProcessing() {
    ProductEntity product = persistProduct(
        UUID.fromString("33333333-3333-3333-3333-333333333333"),
        "USB-C Dock",
        "499.90");
    Order pendingOrder = Order.create(List.of(OrderItem.create(product.getId(), product.getName(), 1, new BigDecimal("499.90"))), FIXED_CLOCK);
    Order processingOrder = Order.create(List.of(OrderItem.create(product.getId(), product.getName(), 2, new BigDecimal("499.90"))), FIXED_CLOCK);
    processingOrder.markProcessing(FIXED_CLOCK);
    orderRepository.save(pendingOrder);
    orderRepository.save(processingOrder);
    entityManager.flush();
    entityManager.clear();

    Instant transitionTime = Instant.parse("2026-06-22T10:30:00Z");
    int updated = orderRepository.transitionPendingOrdersToProcessing(transitionTime);
    entityManager.flush();
    entityManager.clear();

    Optional<Order> transitionedOrder = orderRepository.findById(pendingOrder.getId());
    Optional<Order> untouchedOrder = orderRepository.findById(processingOrder.getId());

    assertThat(updated).isEqualTo(1);
    assertThat(transitionedOrder).isPresent();
    assertThat(transitionedOrder.orElseThrow().getStatus()).isEqualTo(OrderStatus.PROCESSING);
    assertThat(transitionedOrder.orElseThrow().getUpdatedAt()).isEqualTo(transitionTime);
    assertThat(untouchedOrder).isPresent();
    assertThat(untouchedOrder.orElseThrow().getStatus()).isEqualTo(OrderStatus.PROCESSING);
    assertThat(untouchedOrder.orElseThrow().getUpdatedAt()).isEqualTo(FIXED_CLOCK.instant());
  }

  private ProductEntity persistProduct(UUID id, String name, String price) {
    ProductEntity product = ProductEntity.builder()
        .id(id)
        .name(name)
        .price(new BigDecimal(price))
        .build();
    entityManager.persist(product);
    return product;
  }
}
