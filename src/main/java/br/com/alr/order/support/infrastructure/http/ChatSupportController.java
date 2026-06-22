package br.com.alr.order.support.infrastructure.http;

import br.com.alr.order.shared.infrastructure.http.dto.ApiErrorResponse;
import br.com.alr.order.support.application.dto.SupportChatCommand;
import br.com.alr.order.support.application.dto.SupportChatResponse;
import br.com.alr.order.support.application.port.in.ChatSupportUseCase;
import br.com.alr.order.support.infrastructure.http.dto.SupportChatRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Support")
@RestController
@RequestMapping("/support/chat")
public class ChatSupportController {

  private final ChatSupportUseCase chatSupportUseCase;

  public ChatSupportController(ChatSupportUseCase chatSupportUseCase) {
    this.chatSupportUseCase = chatSupportUseCase;
  }

  @Operation(
      summary = "Chat with the AI support agent",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Support response returned"),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid request",
              content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "503",
              description = "AI provider unavailable",
              content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
          )
      }
  )
  @PostMapping
  public SupportChatResponse chat(@Valid @RequestBody SupportChatRequest request) {
    return chatSupportUseCase.chat(
        new SupportChatCommand(request.message(), request.orderId())
    );
  }
}
