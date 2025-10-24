package com.ettdata.credit_service.domain.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreditTransactionResponse {
  private String id;
  private String creditId;
  private CreditTransactionType type;
  private BigDecimal amount;
  private String description;
  private LocalDateTime transactionDate;
}
