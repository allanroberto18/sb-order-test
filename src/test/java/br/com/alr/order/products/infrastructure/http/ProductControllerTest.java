package br.com.alr.order.products.infrastructure.http;

import br.com.alr.order.products.application.dto.ProductDto;
import br.com.alr.order.products.application.port.in.ListProductsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

  private MockMvc mockMvc;
  private ListProductsUseCase listProductsUseCase;

  @BeforeEach
  void setUp() {
    listProductsUseCase = mock(ListProductsUseCase.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(listProductsUseCase)).build();
  }

  @Test
  void shouldListProducts() throws Exception {
    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    when(listProductsUseCase.list()).thenReturn(List.of(new ProductDto(productId, "Notebook Pro 14", new BigDecimal("7499.90"))));

    mockMvc.perform(get("/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(productId.toString()))
        .andExpect(jsonPath("$[0].name").value("Notebook Pro 14"));
  }
}
