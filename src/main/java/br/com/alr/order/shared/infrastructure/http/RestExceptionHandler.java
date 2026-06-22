package br.com.alr.order.shared.infrastructure.http;

import br.com.alr.order.orderitems.application.exception.InvalidOrderItemException;
import br.com.alr.order.orders.application.exception.InvalidOrderException;
import br.com.alr.order.orders.application.exception.OrderCancellationNotAllowedException;
import br.com.alr.order.orders.application.exception.OrderNotFoundException;
import br.com.alr.order.orders.application.exception.ProductNotFoundException;
import br.com.alr.order.shared.infrastructure.http.dto.ApiErrorResponse;
import br.com.alr.order.support.application.exception.AiSupportUnavailableException;
import br.com.alr.order.support.application.exception.InvalidSupportRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(OrderNotFoundException.class)
  ResponseEntity<ApiErrorResponse> handleOrderNotFound(OrderNotFoundException exception, HttpServletRequest request) {
    return error(HttpStatus.NOT_FOUND, exception.getMessage(), request);
  }

  @ExceptionHandler(ProductNotFoundException.class)
  ResponseEntity<ApiErrorResponse> handleProductNotFound(ProductNotFoundException exception, HttpServletRequest request) {
    return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
  }

  @ExceptionHandler({InvalidOrderException.class, InvalidOrderItemException.class, InvalidSupportRequestException.class})
  ResponseEntity<ApiErrorResponse> handleInvalidPayload(RuntimeException exception, HttpServletRequest request) {
    return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
  }

  @ExceptionHandler(OrderCancellationNotAllowedException.class)
  ResponseEntity<ApiErrorResponse> handleCancellationConflict(OrderCancellationNotAllowedException exception,
                                                              HttpServletRequest request) {
    return error(HttpStatus.CONFLICT, exception.getMessage(), request);
  }

  @ExceptionHandler(AiSupportUnavailableException.class)
  ResponseEntity<ApiErrorResponse> handleAiSupportUnavailable(AiSupportUnavailableException exception,
                                                              HttpServletRequest request) {
    return error(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception,
                                                    HttpServletRequest request) {
    String message = exception.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(FieldError::getDefaultMessage)
        .orElse("validation failed");
    return error(HttpStatus.BAD_REQUEST, message, request);
  }

  private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message, HttpServletRequest request) {
    return ResponseEntity.status(status).body(new ApiErrorResponse(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI()));
  }
}
