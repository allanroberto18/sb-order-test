package br.com.alr.order.orders.infrastructure.http;

import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.dto.OrderItemDto;
import br.com.alr.order.orders.application.exception.OrderNotFoundException;
import br.com.alr.order.orders.application.port.in.GetOrderUseCase;
import br.com.alr.order.orders.domain.OrderStatus;
import br.com.alr.order.shared.infrastructure.http.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetOrderControllerTest {

  private MockMvc mockMvc;
  private GetOrderUseCase getOrderUseCase;

  @BeforeEach
  void setUp() {
    getOrderUseCase = mock(GetOrderUseCase.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new GetOrderController(getOrderUseCase))
        .setControllerAdvice(new RestExceptionHandler())
        .build();
  }

  @Test
  void shouldReturnOrderByGetId() throws Exception {
    UUID orderId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    UUID itemId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    when(getOrderUseCase.getById(orderId)).thenReturn(new OrderDto(
        orderId,
        OrderStatus.PENDING,
        new BigDecimal("7499.90"),
        Instant.parse("2026-06-20T20:00:00Z"),
        Instant.parse("2026-06-20T20:00:00Z"),
        List.of(new OrderItemDto(itemId, productId, "Notebook Pro 14", 1, new BigDecimal("7499.90"), new BigDecimal("7499.90")))));

    mockMvc.perform(get("/orders/{orderId}", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(orderId.toString()))
        .andExpect(jsonPath("$.items[0].productName").value("Notebook Pro 14"));
  }

  @Test
  void shouldMapNotFoundError() throws Exception {
    UUID orderId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    when(getOrderUseCase.getById(orderId)).thenThrow(new OrderNotFoundException(orderId));

    mockMvc.perform(get("/orders/{orderId}", orderId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.path").value("/orders/" + orderId));
  }
}
