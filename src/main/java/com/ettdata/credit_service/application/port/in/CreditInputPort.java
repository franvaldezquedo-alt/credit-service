package com.ettdata.credit_service.application.port.in;

import com.ettdata.credit_service.domain.model.CreditListResponse;
import com.ettdata.credit_service.domain.model.CreditResponse;
import com.ettdata.credit_service.infrastructure.model.CreditRequest;
import reactor.core.publisher.Mono;

public interface CreditInputPort {
  Mono<CreditListResponse> getAllCredits();
  Mono<CreditResponse> getCreditById(String creditId);
  Mono<CreditListResponse> getCreditsByDocumentNumber(String documentNumber);
  Mono<CreditResponse> createCredit(CreditRequest creditRequest);
  Mono<CreditResponse> updateCredit(String id, CreditRequest creditRequest);
  Mono<CreditResponse> cancelCredit(String creditId);
  Mono<CreditResponse> markAsOverdue(String creditId);
  Mono<CreditResponse> deleteCredit(String creditId);

}
