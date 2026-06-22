package br.com.alr.order.shared.application.port.out;

public interface DistributedLockRepository {

  boolean tryAcquire(String lockName);

  void release(String lockName);
}
