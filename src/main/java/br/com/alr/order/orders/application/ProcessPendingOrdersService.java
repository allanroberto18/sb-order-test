package br.com.alr.order.orders.application;

import br.com.alr.order.orders.application.port.in.ProcessPendingOrdersUseCase;
import br.com.alr.order.orders.application.port.out.OrderRepository;
import br.com.alr.order.shared.application.port.out.DistributedLockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

@Service
@Transactional
public class ProcessPendingOrdersService implements ProcessPendingOrdersUseCase {

  static final String ORDER_PROCESSING_LOCK = "orders:pending-to-processing";

  private final OrderRepository orderRepository;
  private final DistributedLockRepository distributedLockRepository;
  private final Clock clock;

  public ProcessPendingOrdersService(
      OrderRepository orderRepository,
      DistributedLockRepository distributedLockRepository,
      Clock clock
  ) {
    this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository must not be null");
    this.distributedLockRepository = Objects.requireNonNull(distributedLockRepository, "distributedLockRepository must not be null");
    this.clock = Objects.requireNonNull(clock, "clock must not be null");
  }

  @Override
  public int processPendingOrders() {
    if (!distributedLockRepository.tryAcquire(ORDER_PROCESSING_LOCK)) {
      return 0;
    }

    try {
      Instant updatedAt = Instant.now(clock);
      return orderRepository.transitionPendingOrdersToProcessing(updatedAt);
    } finally {
      distributedLockRepository.release(ORDER_PROCESSING_LOCK);
    }
  }
}
