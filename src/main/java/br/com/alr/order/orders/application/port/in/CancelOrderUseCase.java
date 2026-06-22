package br.com.alr.order.orders.application.port.in;

import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.exception.OrderCancellationNotAllowedException;
import br.com.alr.order.orders.application.exception.OrderNotFoundException;

import java.util.UUID;

public interface CancelOrderUseCase {

  OrderDto cancel(UUID orderId) throws OrderNotFoundException, OrderCancellationNotAllowedException;
}
