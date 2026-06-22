package br.com.alr.order.support.infrastructure.llm;

public class SupportToolExecutionState {

  private boolean attempted;
  private boolean succeeded;

  public void markAttempted() {
    this.attempted = true;
  }

  public void markSucceeded() {
    this.succeeded = true;
  }

  public boolean isAttempted() {
    return attempted;
  }

  public boolean isSucceeded() {
    return succeeded;
  }
}
