package com.ettdata.credit_service.domain.error;

public class CreditNotFoundException extends RuntimeException {
  public CreditNotFoundException(String message) {
    super(message);
  }
}
