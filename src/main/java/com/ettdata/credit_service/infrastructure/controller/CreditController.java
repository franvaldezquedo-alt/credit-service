package com.ettdata.credit_service.infrastructure.controller;

import com.ettdata.credit_service.application.port.in.CreditInputPort;
import com.ettdata.credit_service.domain.model.CreditListResponse;
import com.ettdata.credit_service.domain.model.CreditResponse;
import com.ettdata.credit_service.infrastructure.model.CreditRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/credits")
@CrossOrigin
public class CreditController {

  private final CreditInputPort inputPort;

  public CreditController(CreditInputPort inputPort) {
    this.inputPort = inputPort;
  }

  @PostMapping("/save")
  Mono<CreditResponse> createCredit(@RequestBody @Valid CreditRequest request) {
    return inputPort.createCredit(request);
  }

  @GetMapping("/all")
  Mono<CreditListResponse> getAllCredits() {
    return inputPort.getAllCredits();
  }

  @DeleteMapping("/delete/{id}")
  Mono<CreditResponse> deleteCreditById(@PathVariable String id) {
    return inputPort.deleteCredit(id);
  }

  @GetMapping("/{documentNumber}")
  Mono<CreditListResponse> getCreditsByDocumentNumber(@PathVariable String documentNumber) {
    return inputPort.getCreditsByDocumentNumber(documentNumber);
  }
}
