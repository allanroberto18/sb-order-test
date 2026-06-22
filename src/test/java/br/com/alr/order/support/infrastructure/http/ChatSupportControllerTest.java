package br.com.alr.order.support.infrastructure.http;

import br.com.alr.order.orders.domain.OrderStatus;
import br.com.alr.order.shared.infrastructure.http.RestExceptionHandler;
import br.com.alr.order.support.application.dto.SupportChatResponse;
import br.com.alr.order.support.application.port.in.ChatSupportUseCase;
import br.com.alr.order.support.domain.SupportIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatSupportControllerTest {

  private MockMvc mockMvc;
  private ChatSupportUseCase chatSupportUseCase;

  @BeforeEach
  void setUp() {
    chatSupportUseCase = mock(ChatSupportUseCase.class);
    mockMvc = MockMvcBuilders.standaloneSetup(new ChatSupportController(chatSupportUseCase))
        .setControllerAdvice(new RestExceptionHandler())
        .build();
  }

  @Test
  void shouldReturnChatResponse() throws Exception {
    UUID orderId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    when(chatSupportUseCase.chat(any())).thenReturn(new SupportChatResponse(
        "Your order has been cancelled.",
        SupportIntent.CANCELLATION_REQUEST,
        orderId,
        OrderStatus.CANCELLED,
        true,
        true));

    mockMvc.perform(post("/support/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"message":"Please cancel my order","orderId":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"}
                """.trim()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Your order has been cancelled."))
        .andExpect(jsonPath("$.intent").value("CANCELLATION_REQUEST"))
        .andExpect(jsonPath("$.orderStatus").value("CANCELLED"))
        .andExpect(jsonPath("$.toolAttempted").value(true))
        .andExpect(jsonPath("$.toolSucceeded").value(true));
  }
}
