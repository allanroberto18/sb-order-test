package br.com.alr.order;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class KnowledgeBaseClasspathTest {

  @Test
  void knowledgeBaseResourceIsAvailableOnClasspath() throws IOException {
    try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("knowledge_base.json")) {
      assertNotNull(inputStream, "knowledge_base.json should be available on the classpath");
      assertFalse(inputStream.readAllBytes().length == 0, "knowledge_base.json should not be empty");
    }
  }
}
