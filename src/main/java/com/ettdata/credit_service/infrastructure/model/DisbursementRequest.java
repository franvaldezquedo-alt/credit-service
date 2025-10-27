package com.ettdata.credit_service.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class DisbursementRequest {
  private String creditId;

  @NotNull(message = "El monto no puede estar vacío")
  @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
  private BigDecimal amount;
}
