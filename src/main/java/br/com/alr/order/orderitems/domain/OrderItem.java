package br.com.alr.order.orderitems.domain;

import br.com.alr.order.orderitems.application.exception.InvalidOrderItemException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
public final class OrderItem {

  private final UUID id;
  private final UUID productId;
  private final String productName;
  private final int quantity;
  private final BigDecimal unitPrice;

  private OrderItem(
      UUID id,
      UUID productId,
      String productName,
      int quantity,
      BigDecimal unitPrice
  ) throws InvalidOrderItemException {
    this.id = requireId(id);
    this.productId = requireProductId(productId);
    this.productName = requireProductName(productName);
    this.quantity = requireQuantity(quantity);
    this.unitPrice = requireUnitPrice(unitPrice);
  }

  public static OrderItem create(
      UUID productId,
      String productName,
      int quantity,
      BigDecimal unitPrice
  ) throws InvalidOrderItemException {

    return new OrderItem(UUID.randomUUID(), productId, productName, quantity, unitPrice);
  }

  public static OrderItem restore(
      UUID id,
      UUID productId,
      String productName,
      int quantity,
      BigDecimal unitPrice
  ) throws InvalidOrderItemException {
    return new OrderItem(id, productId, productName, quantity, unitPrice);
  }

  private static UUID requireId(UUID id) {
    return Objects.requireNonNull(id, "order item id must not be null");
  }

  private static UUID requireProductId(UUID productId) {
    return Objects.requireNonNull(productId, "product id must not be null");
  }

  private static String requireProductName(String productName) throws InvalidOrderItemException {
    if (productName == null || productName.isBlank()) {
      throw new InvalidOrderItemException("product name must not be blank");
    }

    return productName;
  }

  private static int requireQuantity(int quantity) throws InvalidOrderItemException {
    if (quantity <= 0) {
      throw new InvalidOrderItemException("quantity must be greater than zero");
    }

    return quantity;
  }

  private static BigDecimal requireUnitPrice(BigDecimal unitPrice) throws InvalidOrderItemException {
    if (unitPrice == null) {
      throw new InvalidOrderItemException("unit price must not be null");
    }

    if (unitPrice.signum() <= 0) {
      throw new InvalidOrderItemException("unit price must be greater than zero");
    }

    return unitPrice;
  }

  public BigDecimal totalPrice() {
    return unitPrice.multiply(BigDecimal.valueOf(quantity));
  }
}
