package br.com.alr.order.support.application.exception;

public class AiSupportUnavailableException extends RuntimeException {

  public AiSupportUnavailableException(String message) {
    super(message);
  }

  public AiSupportUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
