package br.com.alr.order.orderitems.application.exception;

public class InvalidOrderItemException extends RuntimeException {

  public InvalidOrderItemException(String message) {
    super(message);
  }
}
