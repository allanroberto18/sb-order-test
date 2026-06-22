package br.com.alr.order.orders.application.port.out;

import java.util.List;

public record OrderPage<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
}
