package br.com.alr.order.support.application.dto;

import java.util.UUID;

public record SupportChatCommand(String message, UUID orderId) {
}
