package br.com.alr.order.orders.application;

import br.com.alr.order.orderitems.application.port.in.ToOrderItemUseCase;
import br.com.alr.order.orders.application.dto.CreateOrderCommand;
import br.com.alr.order.orders.application.dto.CreateOrderItemCommand;
import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.dto.OrderSummaryDto;
import br.com.alr.order.orders.application.exception.ProductNotFoundException;
import br.com.alr.order.orders.application.port.out.OrderPage;
import br.com.alr.order.orders.domain.Order;
import br.com.alr.order.orders.domain.OrderStatus;
import br.com.alr.order.products.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CreateOrderServiceTest {

  private static final UUID EXISTING_PRODUCT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-06-20T20:00:00Z"), ZoneOffset.UTC);

  @Test
  void shouldCreatePendingOrderWhenAllProductsExist() {
    InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
    InMemoryProductRepository productRepository = new InMemoryProductRepository();
    productRepository.save(new Product(EXISTING_PRODUCT_ID, "Notebook Pro 14", new BigDecimal("7499.90")));
    InMemoryToOrderItemUseCase toOrderItemUseCase = new InMemoryToOrderItemUseCase(productRepository);

    CreateOrderService service = new CreateOrderService(orderRepository, toOrderItemUseCase, FIXED_CLOCK);

    OrderDto order = service.create(new CreateOrderCommand(List.of(
        new CreateOrderItemCommand(EXISTING_PRODUCT_ID, 2, new BigDecimal("7499.90")))));

    assertNotNull(order.id());
    assertEquals(OrderStatus.PENDING, order.status());
    assertEquals("Notebook Pro 14", order.items().getFirst().productName());
    assertEquals(new BigDecimal("14999.80"), order.totalAmount());
    assertEquals(1, orderRepository.savedOrdersCount());
  }

  @Test
  void shouldRejectOrderWhenProductDoesNotExist() {
    InMemoryProductRepository productRepository = new InMemoryProductRepository();
    CreateOrderService service = new CreateOrderService(
        new InMemoryOrderRepository(),
        new InMemoryToOrderItemUseCase(productRepository),
        FIXED_CLOCK);

    assertThrows(ProductNotFoundException.class,
        () -> service.create(new CreateOrderCommand(List.of(
            new CreateOrderItemCommand(EXISTING_PRODUCT_ID, 1, new BigDecimal("7499.90"))))));
  }

  private static final class InMemoryOrderRepository implements br.com.alr.order.orders.application.port.out.OrderRepository {

    private final Map<UUID, Order> orders = new HashMap<>();

    @Override
    public Order save(Order order) {
      orders.put(order.getId(), order);
      return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
      return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public Optional<Order> findDetailedById(UUID orderId) {
      return findById(orderId);
    }

    @Override
    public OrderPage<OrderSummaryDto> findPage(OrderStatus status, int page, int size) {
      List<OrderSummaryDto> content = orders.values().stream()
          .filter(order -> status == null || order.getStatus() == status)
          .map(OrderSummaryDto::from)
          .toList();
      return new OrderPage<>(content, page, size, content.size(), 1);
    }

    @Override
    public List<Order> findAll() {
      return orders.values().stream().toList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
      return orders.values().stream().filter(order -> order.getStatus() == status).toList();
    }

    @Override
    public int transitionPendingOrdersToProcessing(Instant updatedAt) {
      return 0;
    }

    int savedOrdersCount() {
      return orders.size();
    }
  }


  private static final class InMemoryToOrderItemUseCase implements ToOrderItemUseCase {

    private final InMemoryProductRepository productRepository;

    private InMemoryToOrderItemUseCase(InMemoryProductRepository productRepository) {
      this.productRepository = productRepository;
    }

    @Override
    public br.com.alr.order.orderitems.domain.OrderItem toOrderItem(CreateOrderItemCommand item) throws ProductNotFoundException {
      Product product = productRepository.findById(item.productId())
          .orElseThrow(() -> new ProductNotFoundException(item.productId()));
      return br.com.alr.order.orderitems.domain.OrderItem.create(product.id(), product.name(), item.quantity(), item.unitPrice());
    }
  }

  private static final class InMemoryProductRepository implements br.com.alr.order.products.application.port.out.ProductRepository {

    private final Map<UUID, Product> products = new HashMap<>();

    @Override
    public boolean existsById(UUID productId) {
      return products.containsKey(productId);
    }

    @Override
    public Optional<Product> findById(UUID productId) {
      return Optional.ofNullable(products.get(productId));
    }

    @Override
    public List<Product> findAll() {
      return products.values().stream().toList();
    }

    void save(Product product) {
      products.put(product.id(), product);
    }
  }
}
