package br.com.alr.order.orders.infrastructure.http;

import br.com.alr.order.orders.application.dto.OrderSummaryDto;
import br.com.alr.order.orders.application.port.in.ListOrdersUseCase;
import br.com.alr.order.orders.domain.OrderStatus;
import br.com.alr.order.shared.application.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders")
@RestController
@RequestMapping("/orders")
public class ListOrdersController {

  private final ListOrdersUseCase listOrdersUseCase;

  public ListOrdersController(ListOrdersUseCase listOrdersUseCase) {
    this.listOrdersUseCase = listOrdersUseCase;
  }

  @Operation(
      summary = "List orders",
      responses = {
          @ApiResponse(responseCode = "200", description = "Paginated list of orders")
      }
  )
  @GetMapping
  public ResponseEntity<PageDto<OrderSummaryDto>> list(
      @Parameter(description = "Filter by status")
      @RequestParam(required = false) OrderStatus status,
      @Parameter(description = "Page number (0-based)")
      @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size")
      @RequestParam(defaultValue = "20") int size
  ) {
    return ResponseEntity.ok(
        listOrdersUseCase.list(status, page, size)
    );
  }
}
