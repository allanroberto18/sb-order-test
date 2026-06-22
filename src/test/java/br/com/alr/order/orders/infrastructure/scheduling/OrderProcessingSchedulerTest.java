package br.com.alr.order.orders.infrastructure.scheduling;

import br.com.alr.order.orders.application.port.in.ProcessPendingOrdersUseCase;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class OrderProcessingSchedulerTest {

  @Test
  void shouldInvokePendingOrdersProcessingUseCase() {
    ProcessPendingOrdersUseCase useCase = mock(ProcessPendingOrdersUseCase.class);
    when(useCase.processPendingOrders()).thenReturn(3);
    OrderProcessingScheduler scheduler = new OrderProcessingScheduler(useCase);

    scheduler.processPendingOrders();

    verify(useCase).processPendingOrders();
  }
}
