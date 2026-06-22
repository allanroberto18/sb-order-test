package br.com.alr.order.orders.infrastructure.http;

import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.exception.OrderCancellationNotAllowedException;
import br.com.alr.order.orders.application.port.in.CancelOrderUseCase;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CancelOrderControllerTest {

  private MockMvc mockMvc;
  private CancelOrderUseCase cancelOrderUseCase;

  @BeforeEach
  void setUp() {
    cancelOrderUseCase = mock(CancelOrderUseCase.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new CancelOrderController(cancelOrderUseCase))
        .setControllerAdvice(new RestExceptionHandler())
        .build();
  }

  @Test
  void shouldCancelOrder() throws Exception {
    UUID orderId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    when(cancelOrderUseCase.cancel(orderId)).thenReturn(new OrderDto(
        orderId,
        OrderStatus.CANCELLED,
        new BigDecimal("7499.90"),
        Instant.parse("2026-06-20T20:00:00Z"),
        Instant.parse("2026-06-20T20:05:00Z"),
        List.of()));

    mockMvc.perform(post("/orders/{orderId}/cancel", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
  }

  @Test
  void shouldMapCancellationConflict() throws Exception {
    UUID orderId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    when(cancelOrderUseCase.cancel(orderId)).thenThrow(new OrderCancellationNotAllowedException(OrderStatus.PROCESSING));

    mockMvc.perform(post("/orders/{orderId}/cancel", orderId))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409));
  }
}
