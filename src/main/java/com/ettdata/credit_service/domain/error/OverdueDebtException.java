package com.ettdata.credit_service.domain.error;

public class OverdueDebtException extends RuntimeException {
  public OverdueDebtException(String message) {
    super(message);
  }
}
