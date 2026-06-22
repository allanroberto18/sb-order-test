package br.com.alr.order.products.infrastructure.http;

import br.com.alr.order.products.application.dto.ProductDto;
import br.com.alr.order.products.application.port.in.ListProductsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Products")
@RestController
@RequestMapping("/products")
public class ProductController {

  private final ListProductsUseCase listProductsUseCase;

  public ProductController(ListProductsUseCase listProductsUseCase) {
    this.listProductsUseCase = listProductsUseCase;
  }

  @Operation(
      summary = "List all products",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "List of available products"
          )
      }
  )
  @GetMapping
  public ResponseEntity<List<ProductDto>> list() {
    return ResponseEntity.ok(
        listProductsUseCase.list()
    );
  }
}
