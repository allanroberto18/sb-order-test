package br.com.alr.order.support.infrastructure.llm;

import br.com.alr.order.support.application.dto.SupportAiRequest;
import br.com.alr.order.support.application.dto.SupportAiResult;
import br.com.alr.order.support.application.exception.AiSupportUnavailableException;
import br.com.alr.order.support.application.port.out.SupportAiRepository;
import br.com.alr.order.support.infrastructure.configuration.AiSupportProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class SpringAiSupportRepository implements SupportAiRepository {

  private final AiSupportProperties properties;
  private final ChatClient chatClient;
  private final OrderSupportTools orderSupportTools;

  public SpringAiSupportRepository(
      AiSupportProperties properties,
      ChatModel chatModel,
      OrderSupportTools orderSupportTools
  ) {
    this.properties = Objects.requireNonNull(properties, "properties must not be null");
    this.chatClient = ChatClient.builder(Objects.requireNonNull(chatModel, "chatModel must not be null")).build();
    this.orderSupportTools = Objects.requireNonNull(orderSupportTools, "orderSupportTools must not be null");
  }

  @Override
  public SupportAiResult chat(SupportAiRequest request) throws AiSupportUnavailableException {
    ensureOpenAiIsConfigured();
    long startedAt = System.currentTimeMillis();

    try {
      ChatClient.ChatClientRequestSpec prompt = chatClient.prompt()
          .system(request.instructions())
          .user(request.userMessage())
          .options(
              OpenAiChatOptions.builder()
                  .model(properties.model())
                  .temperature(0d)
          );

      SupportToolExecutionState toolState = new SupportToolExecutionState();

      if (!request.tools().isEmpty()) {
        Map<String, Object> toolContext = new HashMap<>();
        toolContext.put("toolState", toolState);
        if (request.orderId() != null) {
          toolContext.put("orderId", request.orderId().toString());
        }

        prompt = prompt.tools(orderSupportTools)
            .toolContext(toolContext);
      }

      ChatResponse response = prompt.call().chatResponse();
      Usage usage = response.getMetadata() != null ? response.getMetadata().getUsage() : null;

      return new SupportAiResult(
          response.getMetadata() != null ? response.getMetadata().getId() : null,
          response.getResult().getOutput().getText(),
          usage != null && usage.getPromptTokens() != null ? usage.getPromptTokens() : 0,
          usage != null && usage.getCompletionTokens() != null ? usage.getCompletionTokens() : 0,
          usage != null && usage.getTotalTokens() != null ? usage.getTotalTokens() : 0,
          System.currentTimeMillis() - startedAt,
          toolState.isAttempted(),
          toolState.isSucceeded()
      );
    } catch (Exception exception) {
      String message = exception.getMessage();
      Throwable cause = exception.getCause();

      if (cause != null && cause.getMessage() != null && !cause.getMessage().isBlank()) {
        message = cause.getMessage();
      }

      throw new AiSupportUnavailableException(
          message != null && !message.isBlank()
              ? "failed to call Spring AI OpenAI chat model: " + message
              : "failed to call Spring AI OpenAI chat model",
          exception
      );
    }
  }

  private void ensureOpenAiIsConfigured() {
    if (!"openai".equalsIgnoreCase(properties.provider())) {
      throw new AiSupportUnavailableException("AI support is not configured for provider " + properties.provider());
    }

    if (properties.model() == null || properties.model().isBlank() || "none".equalsIgnoreCase(properties.model())) {
      throw new AiSupportUnavailableException("AI support model is not configured");
    }
  }
}
