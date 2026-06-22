package br.com.alr.order.orders.application;

import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.exception.OrderCancellationNotAllowedException;
import br.com.alr.order.orders.application.exception.OrderNotFoundException;
import br.com.alr.order.orders.application.port.in.CancelOrderUseCase;
import br.com.alr.order.orders.application.port.out.OrderRepository;
import br.com.alr.order.orders.domain.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class CancelOrderService implements CancelOrderUseCase {

  private final OrderRepository orderRepository;
  private final Clock clock;

  public CancelOrderService(
      OrderRepository orderRepository,
      Clock clock
  ) {
    this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository must not be null");
    this.clock = Objects.requireNonNull(clock, "clock must not be null");
  }

  @Override
  public OrderDto cancel(UUID orderId) throws OrderNotFoundException, OrderCancellationNotAllowedException {
    Order order = orderRepository.findDetailedById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
    order.cancel(clock);

    return OrderDto.from(orderRepository.save(order));
  }
}
