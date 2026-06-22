package br.com.alr.order.orders.application;

import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.exception.OrderNotFoundException;
import br.com.alr.order.orders.application.port.in.GetOrderUseCase;
import br.com.alr.order.orders.application.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetOrderService implements GetOrderUseCase {

  private final OrderRepository orderRepository;

  public GetOrderService(OrderRepository orderRepository) {
    this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository must not be null");
  }

  @Override
  public OrderDto getById(UUID orderId) throws OrderNotFoundException {
    return orderRepository.findDetailedById(orderId)
        .map(OrderDto::from)
        .orElseThrow(() -> new OrderNotFoundException(orderId));
  }
}
