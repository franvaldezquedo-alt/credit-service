package com.ettdata.credit_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credit {
  private String id;
  private String creditNumber;
  private String customerDocument;
  private CreditType type;
  private BigDecimal creditLimit;
  private BigDecimal currentDebt;
  private BigDecimal availableCredit;
  private BigDecimal interestRate;
  private Integer termMonths;
  private BigDecimal monthlyPayment;
  private LocalDate dueDate;
  private Boolean hasOverdueDebt;
  private CreditStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
