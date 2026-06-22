package br.com.alr.order.orders.application.port.in;

import br.com.alr.order.orders.application.dto.CreateOrderCommand;
import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.exception.InvalidOrderException;
import br.com.alr.order.orders.application.exception.ProductNotFoundException;

public interface CreateOrderUseCase {

  OrderDto create(CreateOrderCommand command) throws ProductNotFoundException, InvalidOrderException;
}
