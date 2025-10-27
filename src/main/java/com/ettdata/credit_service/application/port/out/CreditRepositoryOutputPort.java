package com.ettdata.credit_service.application.port.out;

import com.ettdata.credit_service.domain.model.Credit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditRepositoryOutputPort {
  Flux<Credit> findAllCredit();
  Mono<Credit> saveCredit(Credit credit);
  Mono<Void> deleteByIdCredit(String idCredit);
  Flux<Credit> findByDocumentNumber(String documentNumber);
  Mono<Credit> findById(String idCredit);
}
