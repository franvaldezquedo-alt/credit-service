package com.ettdata.credit_service.domain.error;

public class DuplicateCreditException extends RuntimeException {
  public DuplicateCreditException(String message) {
    super(message);
  }
}
