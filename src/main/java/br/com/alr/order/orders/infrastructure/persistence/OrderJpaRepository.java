package br.com.alr.order.orders.infrastructure.persistence;

import br.com.alr.order.orders.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

  Page<OrderEntity> findAllByStatus(OrderStatus status, Pageable pageable);

  List<OrderEntity> findAllByStatus(OrderStatus status);

  @EntityGraph(attributePaths = {"items", "items.product"})
  @Query("select o from OrderEntity o where o.id = :orderId")
  Optional<OrderEntity> findDetailedById(@Param("orderId") UUID orderId);

  @Modifying
  @Query("""
      update OrderEntity o 
            set o.status = :nextStatus, o.updatedAt = :updatedAt
            where o.status = :currentStatus
      """)
  int updateStatusByCurrentStatus(
      @Param("currentStatus") OrderStatus currentStatus,
      @Param("nextStatus") OrderStatus nextStatus,
      @Param("updatedAt") Instant updatedAt
  );
}
