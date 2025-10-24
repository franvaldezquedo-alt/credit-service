package com.ettdata.credit_service.infrastructure.model;

import com.ettdata.credit_service.domain.model.CreditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditRequest {
  @NotBlank(message = "Customer document is required")
  private String customerDocument;

  @NotNull(message = "Credit type is required")
  private CreditType creditType;

  @NotNull(message = "Credit limit is required")
  @DecimalMin(value = "0.01", message = "Credit limit must be greater than 0")
  private BigDecimal creditLimit;

  @NotNull(message = "Interest rate is required")
  @DecimalMin(value = "0.0", message = "Interest rate must be 0 or greater")
  @DecimalMax(value = "1.0", message = "Interest rate must be between 0 and 1")
  private BigDecimal interestRate;

  @NotNull(message = "Term in months is required")
  @Min(value = 1, message = "Term must be at least 1 month")
  @Max(value = 360, message = "Term cannot exceed 360 months")
  private Integer termMonths;
}
