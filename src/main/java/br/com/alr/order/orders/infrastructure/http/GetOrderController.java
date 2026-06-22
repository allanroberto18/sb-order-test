package br.com.alr.order.orders.infrastructure.http;

import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.port.in.GetOrderUseCase;
import br.com.alr.order.shared.infrastructure.http.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Orders")
@RestController
@RequestMapping("/orders")
public class GetOrderController {

  private final GetOrderUseCase getOrderUseCase;

  public GetOrderController(GetOrderUseCase getOrderUseCase) {
    this.getOrderUseCase = getOrderUseCase;
  }

  @Operation(
      summary = "Get order by ID",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Order found"
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Order not found",
              content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
          )
      }
  )
  @GetMapping("/{orderId}")
  public ResponseEntity<OrderDto> getById(
      @Parameter(description = "Order ID")
      @PathVariable UUID orderId
  ) {
    return ResponseEntity.ok(
        getOrderUseCase.getById(orderId)
    );
  }
}
