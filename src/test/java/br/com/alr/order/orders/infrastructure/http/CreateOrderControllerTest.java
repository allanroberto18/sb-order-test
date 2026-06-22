package br.com.alr.order.orders.infrastructure.http;

import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.dto.OrderItemDto;
import br.com.alr.order.orders.application.port.in.CreateOrderUseCase;
import br.com.alr.order.orders.domain.OrderStatus;
import br.com.alr.order.shared.infrastructure.http.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CreateOrderControllerTest {

  private MockMvc mockMvc;
  private CreateOrderUseCase createOrderUseCase;

  @BeforeEach
  void setUp() {
    createOrderUseCase = mock(CreateOrderUseCase.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new CreateOrderController(createOrderUseCase))
        .setControllerAdvice(new RestExceptionHandler())
        .build();
  }

  @Test
  void shouldCreateOrder() throws Exception {
    UUID orderId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    UUID itemId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    OrderDto orderDto = new OrderDto(
        orderId,
        OrderStatus.PENDING,
        new BigDecimal("7499.90"),
        Instant.parse("2026-06-20T20:00:00Z"),
        Instant.parse("2026-06-20T20:00:00Z"),
        List.of(new OrderItemDto(itemId, productId, "Notebook Pro 14", 1, new BigDecimal("7499.90"), new BigDecimal("7499.90"))));
    when(createOrderUseCase.create(any())).thenReturn(orderDto);

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"items":[{"productId":"11111111-1111-1111-1111-111111111111","quantity":1,"unitPrice":7499.90}]}
                """.trim()))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/orders/aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
        .andExpect(jsonPath("$.id").value(orderId.toString()))
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andExpect(jsonPath("$.items[0].productName").value("Notebook Pro 14"));
  }
}
