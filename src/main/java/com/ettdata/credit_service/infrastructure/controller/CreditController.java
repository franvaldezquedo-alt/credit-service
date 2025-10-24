package com.ettdata.credit_service.infrastructure.controller;

import com.ettdata.credit_service.application.port.in.CreditInputPort;
import com.ettdata.credit_service.domain.model.CreditListResponse;
import com.ettdata.credit_service.domain.model.CreditResponse;
import com.ettdata.credit_service.infrastructure.model.CreditRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/credits")
@CrossOrigin
public class CreditController {

  private final CreditInputPort creditService;

    public CreditController(CreditInputPort creditService) {
        this.creditService = creditService;
    }

    @PostMapping
  public Mono<ResponseEntity<CreditResponse>> create(@RequestBody CreditRequest request) {
    return creditService.createCredit(request).map(ResponseEntity::ok);
  }

  @GetMapping
  public Mono<ResponseEntity<CreditListResponse>> getAll() {
    return creditService.getAllCredits().map(ResponseEntity::ok);
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<CreditResponse>> getById(@PathVariable String id) {
    return creditService.getCreditById(id).map(ResponseEntity::ok);
  }

  @GetMapping("/customer/{document}")
  public Mono<ResponseEntity<CreditListResponse>> getByCustomer(@PathVariable String document) {
    return creditService.getCreditsByDocumentNumber(document).map(ResponseEntity::ok);
  }

  @PutMapping("/{id}")
  public Mono<ResponseEntity<CreditResponse>> update(@PathVariable String id, @RequestBody CreditRequest request) {
    return creditService.updateCredit(id, request).map(ResponseEntity::ok);
  }

  @PatchMapping("/{id}/cancel")
  public Mono<ResponseEntity<CreditResponse>> cancel(@PathVariable String id) {
    return creditService.cancelCredit(id).map(ResponseEntity::ok);
  }

  @PatchMapping("/{id}/overdue")
  public Mono<ResponseEntity<CreditResponse>> overdue(@PathVariable String id) {
    return creditService.markAsOverdue(id).map(ResponseEntity::ok);
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<CreditResponse>> delete(@PathVariable String id) {
    return creditService.deleteCredit(id).map(ResponseEntity::ok);
  }
}
