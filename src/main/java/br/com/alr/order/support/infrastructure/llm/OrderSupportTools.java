package br.com.alr.order.support.infrastructure.llm;

import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.exception.OrderCancellationNotAllowedException;
import br.com.alr.order.orders.application.exception.OrderNotFoundException;
import br.com.alr.order.orders.application.port.in.CancelOrderUseCase;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
public class OrderSupportTools {

  private final CancelOrderUseCase cancelOrderUseCase;

  public OrderSupportTools(CancelOrderUseCase cancelOrderUseCase) {
    this.cancelOrderUseCase = Objects.requireNonNull(cancelOrderUseCase, "cancelOrderUseCase must not be null");
  }

  @Tool(description = "Cancels the current order when the server-side business rules allow it.")
  public String cancelOrder(ToolContext toolContext) {
    SupportToolExecutionState state = toolState(toolContext);
    state.markAttempted();

    UUID orderId = resolveOrderId(toolContext.getContext());
    if (orderId == null) {
      return "{\"cancelled\":false,\"reason\":\"missing_order_id\"}";
    }

    try {
      OrderDto order = cancelOrderUseCase.cancel(orderId);
      state.markSucceeded();

      return "{\"cancelled\":true,\"orderId\":\"" + order.id() + "\",\"status\":\"" + order.status() + "\"}";
    } catch (OrderNotFoundException exception) {
      return "{\"cancelled\":false,\"reason\":\"order_not_found\"}";
    } catch (OrderCancellationNotAllowedException exception) {
      return "{\"cancelled\":false,\"reason\":\"" + exception.getMessage() + "\"}";
    }
  }

  private SupportToolExecutionState toolState(ToolContext toolContext) {
    Object value = toolContext.getContext().get("toolState");
    if (value instanceof SupportToolExecutionState state) {
      return state;
    }

    return new SupportToolExecutionState();
  }

  private UUID resolveOrderId(Map<String, Object> context) {
    Object rawValue = context.get("orderId");
    if (rawValue == null) {
      return null;
    }

    try {
      return UUID.fromString(rawValue.toString());
    } catch (IllegalArgumentException exception) {
      return null;
    }
  }
}
