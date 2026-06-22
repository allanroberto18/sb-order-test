package br.com.alr.order.support.infrastructure.configuration;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Builder
@ConfigurationProperties(prefix = "app.ai")
public record AiSupportProperties(
    String provider,
    String model,
    Duration timeout,
    String knowledgeBaseResource
) {

  public AiSupportProperties {
    provider = provider == null || provider.isBlank() ? "openai" : provider;
    model = model == null || model.isBlank() ? "gpt-4.1-mini" : model;
    timeout = timeout == null ? Duration.ofSeconds(20) : timeout;
    knowledgeBaseResource = knowledgeBaseResource == null || knowledgeBaseResource.isBlank()
        ? "knowledge_base.json"
        : knowledgeBaseResource;
  }
}
