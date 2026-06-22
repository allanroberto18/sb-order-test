package br.com.alr.order.orders.application;

import br.com.alr.order.orders.application.dto.OrderSummaryDto;
import br.com.alr.order.orders.application.port.in.ListOrdersUseCase;
import br.com.alr.order.orders.application.port.out.OrderPage;
import br.com.alr.order.orders.application.port.out.OrderRepository;
import br.com.alr.order.orders.domain.OrderStatus;
import br.com.alr.order.shared.application.dto.PageDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ListOrdersService implements ListOrdersUseCase {

  private final OrderRepository orderRepository;

  public ListOrdersService(OrderRepository orderRepository) {
    this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository must not be null");
  }

  @Override
  public PageDto<OrderSummaryDto> list(OrderStatus status, int page, int size) {
    OrderPage<OrderSummaryDto> orderPage = orderRepository.findPage(status, page, size);

    return new PageDto<>(
        orderPage.content(),
        orderPage.page(),
        orderPage.size(),
        orderPage.totalElements(),
        orderPage.totalPages()
    );
  }
}
