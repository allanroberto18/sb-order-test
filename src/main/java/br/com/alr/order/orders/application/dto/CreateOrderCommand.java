package br.com.alr.order.orders.application.dto;

import java.util.List;

public record CreateOrderCommand(List<CreateOrderItemCommand> items) {
}
