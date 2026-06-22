package br.com.alr.order.orders.application;

import br.com.alr.order.orderitems.application.port.in.ToOrderItemUseCase;
import br.com.alr.order.orderitems.domain.OrderItem;
import br.com.alr.order.orders.application.dto.CreateOrderCommand;
import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.exception.InvalidOrderException;
import br.com.alr.order.orders.application.exception.ProductNotFoundException;
import br.com.alr.order.orders.application.port.in.CreateOrderUseCase;
import br.com.alr.order.orders.application.port.out.OrderRepository;
import br.com.alr.order.orders.domain.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CreateOrderService implements CreateOrderUseCase {

  private final OrderRepository orderRepository;
  private final ToOrderItemUseCase toOrderItemUseCase;
  private final Clock clock;

  public CreateOrderService(
      OrderRepository orderRepository,
      ToOrderItemUseCase toOrderItemUseCase,
      Clock clock
  ) {
    this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository must not be null");
    this.toOrderItemUseCase = Objects.requireNonNull(toOrderItemUseCase, "toOrderItemUseCase must not be null");
    this.clock = Objects.requireNonNull(clock, "clock must not be null");
  }

  @Override
  public OrderDto create(CreateOrderCommand command) throws ProductNotFoundException, InvalidOrderException {
    Objects.requireNonNull(command, "command must not be null");

    List<OrderItem> items = command.items().stream()
        .map(toOrderItemUseCase::toOrderItem)
        .toList();

    Order order = Order.create(items, clock);
    return OrderDto.from(orderRepository.save(order));
  }
}
