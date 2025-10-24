package com.ettdata.credit_service.infrastructure.utils;

import com.ettdata.credit_service.domain.model.CreditResponse;
import org.springframework.stereotype.Component;

@Component
public class CreditMapperResponse {

  public CreditResponse success(int code, String message, String entityId) {
    return CreditResponse.builder()
          .codResponse(code)
          .messageResponse(message)
          .codEntity(entityId)
          .build();
  }

  public CreditResponse error(int code, String message) {
    return CreditResponse.builder()
          .codResponse(code)
          .messageResponse(message)
          .codEntity(null)
          .build();
  }

  public CreditResponse notFound(String message) {
    return error(404, message);
  }

  public CreditResponse badRequest(String message) {
    return error(400, message);
  }

  public CreditResponse conflict(String message) {
    return error(409, message);
  }

  public CreditResponse internalError(String message) {
    return error(500, message);
  }
}
