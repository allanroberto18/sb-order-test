package br.com.alr.order.orders.infrastructure.scheduling;

import br.com.alr.order.orders.application.port.in.ProcessPendingOrdersUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessingScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderProcessingScheduler.class);

  private final ProcessPendingOrdersUseCase processPendingOrdersUseCase;

  public OrderProcessingScheduler(ProcessPendingOrdersUseCase processPendingOrdersUseCase) {
    this.processPendingOrdersUseCase = processPendingOrdersUseCase;
  }

  @Scheduled(cron = "${app.scheduling.order-processing-cron}")
  public void processPendingOrders() {
    int updatedOrders = processPendingOrdersUseCase.processPendingOrders();
    LOGGER.info("Processed pending orders batch; updated {} orders", updatedOrders);
  }
}
