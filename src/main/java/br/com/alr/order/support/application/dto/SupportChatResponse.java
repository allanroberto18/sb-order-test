package br.com.alr.order.support.application.dto;

import br.com.alr.order.orders.domain.OrderStatus;
import br.com.alr.order.support.domain.SupportIntent;

import java.util.UUID;

public record SupportChatResponse(
    String message,
    SupportIntent intent,
    UUID orderId,
    OrderStatus orderStatus,
    boolean toolAttempted,
    boolean toolSucceeded
) {
}
