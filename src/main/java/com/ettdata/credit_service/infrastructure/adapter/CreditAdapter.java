package com.ettdata.credit_service.infrastructure.adapter;

import com.ettdata.credit_service.application.port.out.CreditRepositoryOutputPort;
import com.ettdata.credit_service.domain.model.Credit;
import com.ettdata.credit_service.infrastructure.entity.CreditEntity;
import com.ettdata.credit_service.infrastructure.repository.CreditRepository;
import com.ettdata.credit_service.infrastructure.utils.CreditMapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CreditAdapter implements CreditRepositoryOutputPort{

  private final CreditRepository repository;
  private final CreditMapper mapper;

  public CreditAdapter(CreditRepository repository, CreditMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Flux<Credit> findAllCredit() {
    return repository.findAll()
          .map(mapper::toDomain);
  }

  @Override
  public Mono<Credit> saveCredit(Credit credit) {
    return repository.save(mapper.toEntity(credit))
          .map(mapper::toDomain);
  }

  @Override
  public Mono<Void> deleteByIdCredit(String idCredit) {
    return repository.deleteById(idCredit);
  }

  @Override
  public Flux<Credit> findByDocumentNumber(String documentNumber) {
    return repository.findBycustomerDocument(documentNumber)
          .map(mapper::toDomain);
  }

  @Override
  public Mono<Credit> findById(String idCredit) {
    return repository.findById(idCredit)
          .map(mapper::toDomain);
  }
}
