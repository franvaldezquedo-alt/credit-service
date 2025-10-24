package com.ettdata.credit_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditResponse {
  private Integer codResponse;
  private String messageResponse;
  private String codEntity;
}
