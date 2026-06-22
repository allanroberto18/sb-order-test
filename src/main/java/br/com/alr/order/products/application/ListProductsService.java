package br.com.alr.order.products.application;

import br.com.alr.order.products.application.dto.ProductDto;
import br.com.alr.order.products.application.port.in.ListProductsUseCase;
import br.com.alr.order.products.application.port.out.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ListProductsService implements ListProductsUseCase {

  private final ProductRepository productRepository;

  public ListProductsService(ProductRepository productRepository) {
    this.productRepository = Objects.requireNonNull(productRepository, "productRepository must not be null");
  }

  @Override
  public List<ProductDto> list() {
    return productRepository.findAll().stream()
        .map(ProductDto::from)
        .toList();
  }
}
