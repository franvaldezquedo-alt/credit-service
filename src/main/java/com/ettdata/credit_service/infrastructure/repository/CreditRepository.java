package com.ettdata.credit_service.infrastructure.repository;

import com.ettdata.credit_service.infrastructure.entity.CreditEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CreditRepository extends ReactiveMongoRepository<CreditEntity, String> {
    Flux<CreditEntity> findByCustomerDocument(String customerDocument);
}
