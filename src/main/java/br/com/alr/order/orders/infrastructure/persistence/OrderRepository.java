package br.com.alr.order.orders.infrastructure.persistence;

import br.com.alr.order.orders.application.dto.OrderSummaryDto;
import br.com.alr.order.orders.application.port.out.OrderPage;
import br.com.alr.order.orders.domain.Order;
import br.com.alr.order.orders.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class OrderRepository implements br.com.alr.order.orders.application.port.out.OrderRepository {

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 20;
  private static final int MAX_SIZE = 100;

  private final OrderJpaRepository orderJpaRepository;
  private final OrderPersistenceMapper mapper = new OrderPersistenceMapper();

  public OrderRepository(OrderJpaRepository orderJpaRepository) {
    this.orderJpaRepository = orderJpaRepository;
  }

  @Override
  public Order save(Order order) {
    orderJpaRepository.save(mapper.toEntity(order));
    return order;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Order> findById(UUID orderId) {
    return orderJpaRepository.findById(orderId)
        .map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Order> findDetailedById(UUID orderId) {
    return orderJpaRepository.findDetailedById(orderId)
        .map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public OrderPage<OrderSummaryDto> findPage(OrderStatus status, int page, int size) {
    PageRequest pageRequest = PageRequest.of(normalizePage(page), normalizeSize(size));
    Page<OrderEntity> result = status == null
        ? orderJpaRepository.findAll(pageRequest)
        : orderJpaRepository.findAllByStatus(status, pageRequest);
    return new OrderPage<>(
        result.getContent().stream().map(mapper::toSummary).toList(),
        result.getNumber(),
        result.getSize(),
        result.getTotalElements(),
        result.getTotalPages());
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> findAll() {
    return orderJpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> findByStatus(OrderStatus status) {
    return orderJpaRepository.findAllByStatus(status).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public int transitionPendingOrdersToProcessing(Instant updatedAt) {
    return orderJpaRepository.updateStatusByCurrentStatus(
        OrderStatus.PENDING,
        OrderStatus.PROCESSING,
        updatedAt);
  }

  private int normalizePage(int page) {
    return Math.max(page, DEFAULT_PAGE);
  }

  private int normalizeSize(int size) {
    if (size <= 0) {
      return DEFAULT_SIZE;
    }
    return Math.min(size, MAX_SIZE);
  }
}
