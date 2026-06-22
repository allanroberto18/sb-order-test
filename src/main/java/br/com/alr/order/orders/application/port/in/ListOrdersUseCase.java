package br.com.alr.order.orders.application.port.in;

import br.com.alr.order.orders.application.dto.OrderSummaryDto;
import br.com.alr.order.orders.domain.OrderStatus;
import br.com.alr.order.shared.application.dto.PageDto;

public interface ListOrdersUseCase {

  PageDto<OrderSummaryDto> list(OrderStatus status, int page, int size);
}
