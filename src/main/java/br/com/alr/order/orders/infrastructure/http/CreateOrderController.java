package br.com.alr.order.orders.infrastructure.http;

import br.com.alr.order.orders.application.dto.CreateOrderCommand;
import br.com.alr.order.orders.application.dto.CreateOrderItemCommand;
import br.com.alr.order.orders.application.dto.OrderDto;
import br.com.alr.order.orders.application.port.in.CreateOrderUseCase;
import br.com.alr.order.orders.infrastructure.http.dto.CreateOrderRequest;
import br.com.alr.order.shared.infrastructure.http.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Orders")
@RestController
@RequestMapping("/orders")
public class
CreateOrderController {

  private final CreateOrderUseCase createOrderUseCase;

  public CreateOrderController(CreateOrderUseCase createOrderUseCase) {
    this.createOrderUseCase = createOrderUseCase;
  }

  @Operation(
      summary = "Create a new order",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "Order created",
              headers = @Header(name = "Location", description = "URL of the created order")
          ),
          @ApiResponse(
              responseCode = "400",
              description = "Invalid request",
              content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Product not found",
              content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
          )
      }
  )
  @PostMapping
  public ResponseEntity<OrderDto> create(@Valid @RequestBody CreateOrderRequest request) {
    OrderDto createdOrder = createOrderUseCase.create(toCommand(request));
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{orderId}")
        .buildAndExpand(createdOrder.id())
        .toUri();

    return ResponseEntity.created(location).body(createdOrder);
  }

  private CreateOrderCommand toCommand(CreateOrderRequest request) {
    return new CreateOrderCommand(request.items().stream()
        .map(item -> new CreateOrderItemCommand(item.productId(), item.quantity(), item.unitPrice()))
        .toList());
  }
}
