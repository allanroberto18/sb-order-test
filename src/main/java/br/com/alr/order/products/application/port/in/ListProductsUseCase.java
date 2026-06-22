package br.com.alr.order.products.application.port.in;

import br.com.alr.order.products.application.dto.ProductDto;

import java.util.List;

public interface ListProductsUseCase {

  List<ProductDto> list();
}
