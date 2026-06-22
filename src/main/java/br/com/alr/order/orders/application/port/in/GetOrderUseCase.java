package br.com.alr.order.orders.application.port.in;

import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.exception.OrderNotFoundException;

import java.util.UUID;

public interface GetOrderUseCase {

  OrderDto getById(UUID orderId) throws OrderNotFoundException;
}
