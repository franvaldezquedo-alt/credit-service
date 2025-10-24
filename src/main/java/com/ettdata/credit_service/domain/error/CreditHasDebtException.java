package com.ettdata.credit_service.domain.error;

public class CreditHasDebtException extends RuntimeException {
  public CreditHasDebtException(String message) {
    super(message);
  }
}
