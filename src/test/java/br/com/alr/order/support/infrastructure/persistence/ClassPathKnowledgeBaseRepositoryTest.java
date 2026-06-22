package br.com.alr.order.support.infrastructure.persistence;

import br.com.alr.order.support.domain.SupportPolicy;
import br.com.alr.order.support.infrastructure.configuration.AiSupportProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ClassPathKnowledgeBaseRepositoryTest {

  @Test
  void shouldLoadPoliciesFromClasspathResource() {
    AiSupportProperties properties = AiSupportProperties.builder()
        .knowledgeBaseResource("knowledge_base.json")
        .build();

    ClassPathKnowledgeBaseRepository repository = new ClassPathKnowledgeBaseRepository(new ObjectMapper(), properties);

    List<SupportPolicy> policies = repository.findAll();

    assertEquals(2, policies.size());
    assertEquals("Order Cancellations", policies.getFirst().context());
    assertFalse(policies.getFirst().rule().isBlank());
  }
}
