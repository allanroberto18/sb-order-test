package br.com.alr.order.support.application.dto;

public record SupportAiToolResult(
    String previousResponseId,
    String toolCallId,
    String toolName,
    String output,
    String instructions
) {
}
