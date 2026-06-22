package br.com.alr.order.orders.application.port.out;

import br.com.alr.order.orders.application.dto.OrderSummaryDto;
import br.com.alr.order.orders.domain.Order;
import br.com.alr.order.orders.domain.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

  Order save(Order order);

  Optional<Order> findById(UUID orderId);

  Optional<Order> findDetailedById(UUID orderId);

  OrderPage<OrderSummaryDto> findPage(OrderStatus status, int page, int size);

  List<Order> findAll();

  List<Order> findByStatus(OrderStatus status);

  int transitionPendingOrdersToProcessing(Instant updatedAt);
}
