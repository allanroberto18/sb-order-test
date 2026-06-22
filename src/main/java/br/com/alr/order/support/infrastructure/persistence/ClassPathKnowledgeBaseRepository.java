package br.com.alr.order.support.infrastructure.persistence;

import br.com.alr.order.support.domain.SupportPolicy;
import br.com.alr.order.support.infrastructure.configuration.AiSupportProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;

@Repository
public class ClassPathKnowledgeBaseRepository implements br.com.alr.order.support.application.port.out.KnowledgeBaseRepository {

  private final ObjectMapper objectMapper;
  private final AiSupportProperties properties;
  private volatile List<SupportPolicy> cachedPolicies;

  public ClassPathKnowledgeBaseRepository(
      ObjectMapper objectMapper,
      AiSupportProperties properties
  ) {
    this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    this.properties = Objects.requireNonNull(properties, "properties must not be null");
  }

  @Override
  public List<SupportPolicy> findAll() {
    List<SupportPolicy> localCache = cachedPolicies;
    if (localCache != null) {
      return localCache;
    }

    try (InputStream inputStream = new ClassPathResource(properties.knowledgeBaseResource()).getInputStream()) {
      List<SupportPolicy> policies = objectMapper.readValue(
          inputStream,
          new TypeReference<>() {
          }
      );
      cachedPolicies = policies;
      return policies;
    } catch (IOException exception) {
      throw new UncheckedIOException("failed to load knowledge base resource", exception);
    }
  }
}
