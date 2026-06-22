package br.com.alr.order.orders.application;

import br.com.alr.order.orderitems.domain.OrderItem;
import br.com.alr.order.orders.application.dto.OrderSummaryDto;
import br.com.alr.order.orders.application.port.out.OrderPage;
import br.com.alr.order.orders.application.port.out.OrderRepository;
import br.com.alr.order.orders.domain.Order;
import br.com.alr.order.orders.domain.OrderStatus;
import br.com.alr.order.shared.application.port.out.DistributedLockRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ProcessPendingOrdersServiceTest {

  private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-06-20T20:00:00Z"), ZoneOffset.UTC);

  @Test
  void shouldTransitionPendingOrdersWhenLockIsAcquired() {
    InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
    orderRepository.save(Order.restore(
        UUID.randomUUID(),
        List.of(OrderItem.create(UUID.randomUUID(), "Notebook Pro 14", 1, new BigDecimal("10.00"))),
        OrderStatus.PENDING,
        Instant.parse("2026-06-20T19:55:00Z"),
        Instant.parse("2026-06-20T19:55:00Z")));
    StubDistributedLockRepository lockRepository = new StubDistributedLockRepository(true);

    ProcessPendingOrdersService service = new ProcessPendingOrdersService(orderRepository, lockRepository, FIXED_CLOCK);

    int updatedOrders = service.processPendingOrders();

    assertEquals(1, updatedOrders);
    assertTrue(lockRepository.released());
    assertEquals(OrderStatus.PROCESSING, orderRepository.firstOrderStatus());
  }

  @Test
  void shouldSkipTransitionWhenLockIsNotAcquired() {
    InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
    orderRepository.save(Order.restore(
        UUID.randomUUID(),
        List.of(OrderItem.create(UUID.randomUUID(), "Notebook Pro 14", 1, new BigDecimal("10.00"))),
        OrderStatus.PENDING,
        Instant.parse("2026-06-20T19:55:00Z"),
        Instant.parse("2026-06-20T19:55:00Z")));
    StubDistributedLockRepository lockRepository = new StubDistributedLockRepository(false);

    ProcessPendingOrdersService service = new ProcessPendingOrdersService(orderRepository, lockRepository, FIXED_CLOCK);

    int updatedOrders = service.processPendingOrders();

    assertEquals(0, updatedOrders);
    assertFalse(lockRepository.released());
    assertEquals(OrderStatus.PENDING, orderRepository.firstOrderStatus());
  }

  private static final class StubDistributedLockRepository implements DistributedLockRepository {

    private final boolean acquireResult;
    private boolean released;

    private StubDistributedLockRepository(boolean acquireResult) {
      this.acquireResult = acquireResult;
    }

    @Override
    public boolean tryAcquire(String lockName) {
      return acquireResult;
    }

    @Override
    public void release(String lockName) {
      this.released = true;
    }

    boolean released() {
      return released;
    }
  }

  private static final class InMemoryOrderRepository implements OrderRepository {

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
      int updated = 0;
      for (Order order : orders.values()) {
        if (order.getStatus() == OrderStatus.PENDING) {
          order.markProcessing(Clock.fixed(updatedAt, ZoneOffset.UTC));
          updated++;
        }
      }
      return updated;
    }

    OrderStatus firstOrderStatus() {
      return orders.values().stream().findFirst().orElseThrow().getStatus();
    }
  }
}
