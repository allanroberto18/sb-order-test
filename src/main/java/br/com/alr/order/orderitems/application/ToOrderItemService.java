package br.com.alr.order.orderitems.application;

import br.com.alr.order.orderitems.application.port.in.ToOrderItemUseCase;
import br.com.alr.order.orderitems.domain.OrderItem;
import br.com.alr.order.orders.application.dto.CreateOrderItemCommand;
import br.com.alr.order.orders.application.exception.ProductNotFoundException;
import br.com.alr.order.products.application.port.out.ProductRepository;
import br.com.alr.order.products.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ToOrderItemService implements ToOrderItemUseCase {

  private final ProductRepository productRepository;

  public ToOrderItemService(ProductRepository productRepository) {
    this.productRepository = Objects.requireNonNull(productRepository, "productRepository must not be null");
  }

  @Override
  public OrderItem toOrderItem(CreateOrderItemCommand item) throws ProductNotFoundException {
    Product product = productRepository.findById(item.productId())
        .orElseThrow(() -> new ProductNotFoundException(item.productId()));

    return OrderItem.create(
        product.id(),
        product.name(),
        item.quantity(),
        item.unitPrice()
    );
  }
}
