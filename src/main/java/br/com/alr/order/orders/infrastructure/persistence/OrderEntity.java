package br.com.alr.order.orders.infrastructure.persistence;

import br.com.alr.order.orderitems.infrastructure.persistence.OrderItemEntity;
import br.com.alr.order.orders.domain.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderEntity {

  @Id
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private OrderStatus status;

  @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Default
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItemEntity> items = new ArrayList<>();

  public void updateStatus(OrderStatus status) {
    this.status = status;
  }

  public void updateTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public void updateCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public void updateUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void replaceItems(List<OrderItemEntity> nextItems) {
    this.items.clear();
    this.items.addAll(nextItems);
  }
}
