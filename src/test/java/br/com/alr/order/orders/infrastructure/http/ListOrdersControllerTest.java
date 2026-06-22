package br.com.alr.order.orders.infrastructure.http;

import br.com.alr.order.orders.application.dto.OrderSummaryDto;
import br.com.alr.order.orders.application.port.in.ListOrdersUseCase;
import br.com.alr.order.orders.domain.OrderStatus;
import br.com.alr.order.shared.application.dto.PageDto;
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

class ListOrdersControllerTest {

  private MockMvc mockMvc;
  private ListOrdersUseCase listOrdersUseCase;

  @BeforeEach
  void setUp() {
    listOrdersUseCase = mock(ListOrdersUseCase.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new ListOrdersController(listOrdersUseCase)).build();
  }

  @Test
  void shouldListOrdersByGetStatusWithPagination() throws Exception {
    UUID orderId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    when(listOrdersUseCase.list(OrderStatus.PENDING, 1, 5)).thenReturn(new PageDto<>(
        List.of(new OrderSummaryDto(
            orderId,
            OrderStatus.PENDING,
            new BigDecimal("7499.90"),
            Instant.parse("2026-06-20T20:00:00Z"),
            Instant.parse("2026-06-20T20:00:00Z"))),
        1,
        5,
        1,
        1));

    mockMvc.perform(get("/orders")
            .param("status", "PENDING")
            .param("page", "1")
            .param("size", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(orderId.toString()))
        .andExpect(jsonPath("$.content[0].status").value("PENDING"))
        .andExpect(jsonPath("$.page").value(1))
        .andExpect(jsonPath("$.size").value(5));
  }
}
