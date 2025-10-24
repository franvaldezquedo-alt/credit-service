package com.ettdata.credit_service.infrastructure.entity;

import com.ettdata.credit_service.domain.model.CreditStatus;
import com.ettdata.credit_service.domain.model.CreditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "credits")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditEntity {
  @Id
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
