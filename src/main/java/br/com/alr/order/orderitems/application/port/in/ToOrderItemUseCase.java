package br.com.alr.order.orderitems.application.port.in;

import br.com.alr.order.orderitems.domain.OrderItem;
import br.com.alr.order.orders.application.dto.CreateOrderItemCommand;
import br.com.alr.order.orders.application.exception.ProductNotFoundException;

public interface ToOrderItemUseCase {

  OrderItem toOrderItem(CreateOrderItemCommand item) throws ProductNotFoundException;
}
