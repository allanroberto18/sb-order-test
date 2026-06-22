package br.com.alr.order.support.application.dto;

import java.util.List;
import java.util.UUID;

public record SupportAiRequest(
    String instructions,
    String userMessage,
    UUID orderId,
    List<SupportToolDefinition> tools
) { }
