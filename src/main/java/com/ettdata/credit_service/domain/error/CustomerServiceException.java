package com.ettdata.credit_service.domain.error;

public class CustomerServiceException extends RuntimeException {
  public CustomerServiceException(String message) {
    super(message);
  }
}
