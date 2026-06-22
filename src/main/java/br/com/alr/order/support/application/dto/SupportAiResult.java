package br.com.alr.order.support.application.dto;

public record SupportAiResult(
    String responseId,
    String message,
    int inputTokens,
    int outputTokens,
    int totalTokens,
    long elapsedMillis,
    boolean toolAttempted,
    boolean toolSucceeded
) {
}
