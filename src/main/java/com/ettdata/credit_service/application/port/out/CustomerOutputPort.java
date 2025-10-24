package com.ettdata.credit_service.application.port.out;

import com.ettdata.credit_service.domain.model.CustomerResponse;
import reactor.core.publisher.Mono;

public interface CustomerOutputPort {
  Mono<CustomerResponse> getCustomerByDocument(String customerDocument);
}
