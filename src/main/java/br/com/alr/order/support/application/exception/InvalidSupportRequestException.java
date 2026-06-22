package br.com.alr.order.support.application.exception;

public class InvalidSupportRequestException extends RuntimeException {

  public InvalidSupportRequestException(String message) {
    super(message);
  }
}
