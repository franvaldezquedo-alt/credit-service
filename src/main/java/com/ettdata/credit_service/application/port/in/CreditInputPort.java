package com.ettdata.credit_service.application.port.in;

import com.ettdata.credit_service.domain.model.CreditListResponse;
import com.ettdata.credit_service.domain.model.CreditResponse;
import com.ettdata.credit_service.infrastructure.model.CreditRequest;
import reactor.core.publisher.Mono;

public interface CreditInputPort {
  Mono<CreditListResponse> getAllCredits();
  Mono<CreditResponse> createCredit(CreditRequest creditRequest);
  Mono<CreditResponse> deleteCredit(String creditId);
  Mono<CreditListResponse> getCreditsByDocumentNumber(String documentNumber);

}
