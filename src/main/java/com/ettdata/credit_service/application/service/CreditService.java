package com.ettdata.credit_service.application.service;

import com.ettdata.credit_service.application.port.in.CreditInputPort;
import com.ettdata.credit_service.application.port.out.CreditRepositoryOutputPort;
import com.ettdata.credit_service.domain.error.*;
import com.ettdata.credit_service.domain.model.CreditListResponse;
import com.ettdata.credit_service.domain.model.CreditResponse;
import com.ettdata.credit_service.domain.model.CreditStatus;
import com.ettdata.credit_service.infrastructure.model.CreditRequest;
import com.ettdata.credit_service.infrastructure.utils.CreditMapper;
import com.ettdata.credit_service.infrastructure.utils.CreditMapperResponse;
import com.ettdata.credit_service.infrastructure.utils.CreditValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class CreditService implements CreditInputPort {

  private final CreditRepositoryOutputPort repositoryOutputPort;
  private final CreditMapper mapper;
  private final CreditValidator validator;
  private final CreditMapperResponse mapperResponse;

  /**
   * Obtener todos los créditos
   */
  @Override
  public Mono<CreditListResponse> getAllCredits() {
    log.info("Getting all credits");

    return repositoryOutputPort.findAllCredit()
          .collectList()
          .map(credits -> new CreditListResponse(credits, null))
          .doOnSuccess(response ->
                log.info("Retrieved {} credits", response.getData().size()))
          .onErrorResume(error -> {
            log.error("Error getting all credits", error);
            return Mono.just(new CreditListResponse(
                  null,
                  "Error getting all credits: " + error.getMessage()
            ));
          });
  }

  /**
   * Crear un nuevo crédito
   */
  @Override
  public Mono<CreditResponse> createCredit(CreditRequest creditRequest) {
    log.info("Creating credit for customer: {}", creditRequest.getCustomerDocument());

    return validator.validateCreditCreation(creditRequest)
          .map(mapper::requestToDomain)
          .flatMap(repositoryOutputPort::saveCredit)
          .map(credit -> mapperResponse.success(201, "Credit created successfully", credit.getId()))
          .doOnSuccess(response ->
                log.info("Credit created with ID: {}", response.getCodEntity()))
          .onErrorResume(CustomerNotFoundException.class, ex -> {
            log.error("Customer not found", ex);
            return Mono.just(mapperResponse.notFound(ex.getMessage()));
          })
          .onErrorResume(OverdueDebtException.class, ex -> {
            log.error("Customer has overdue debt", ex);
            return Mono.just(mapperResponse.badRequest(ex.getMessage()));
          })
          .onErrorResume(DuplicateCreditException.class, ex -> {
            log.error("Duplicate credit", ex);
            return Mono.just(mapperResponse.conflict(ex.getMessage()));
          })
          .onErrorResume(error -> {
            log.error("Error creating credit", error);
            return Mono.just(mapperResponse.internalError("Error creating credit: " + error.getMessage()));
          });
  }

  /**
   * Eliminar (soft delete) un crédito
   */
  @Override
  public Mono<CreditResponse> deleteCredit(String creditId) {
    log.info("Deleting credit: {}", creditId);

    return validator.validateCreditExistsForDeletion(creditId)
          .flatMap(repositoryOutputPort::findById)
          .flatMap(validator::validateCreditCanBeDeleted)
          .flatMap(credit -> {
            credit.setStatus(CreditStatus.INACTIVE);
            credit.setUpdatedAt(LocalDateTime.now());
            return repositoryOutputPort.saveCredit(credit);
          })
          .map(credit -> mapperResponse.success(200, "Credit deleted successfully", credit.getId()))
          .doOnSuccess(response ->
                log.info("Credit deleted: {}", response.getCodEntity()))
          .onErrorResume(CreditNotFoundException.class, ex -> {
            log.error("Credit not found", ex);
            return Mono.just(mapperResponse.notFound(ex.getMessage()));
          })
          .onErrorResume(CreditHasDebtException.class, ex -> {
            log.error("Credit has pending debt", ex);
            return Mono.just(mapperResponse.badRequest(ex.getMessage()));
          })
          .onErrorResume(error -> {
            log.error("Error deleting credit", error);
            return Mono.just(mapperResponse.internalError("Error deleting credit: " + error.getMessage()));
          });
  }

  /**
   * Obtener créditos por número de documento
   */
  @Override
  public Mono<CreditListResponse> getCreditsByDocumentNumber(String documentNumber) {
    log.info("Getting credits for customer: {}", documentNumber);

    return repositoryOutputPort.findByDocumentNumber(documentNumber)
          .collectList()
          .flatMap(credits -> {
            if (credits.isEmpty()) {
              log.warn("No credits found for customer: {}", documentNumber);
              return Mono.just(new CreditListResponse(
                    credits,
                    "No credits found for customer: " + documentNumber
              ));
            }
            log.info("Found {} credits for customer: {}", credits.size(), documentNumber);
            return Mono.just(new CreditListResponse(credits, null));
          })
          .onErrorResume(error -> {
            log.error("Error getting credits for customer: {}", documentNumber, error);
            return Mono.just(new CreditListResponse(
                  null,
                  "Error getting credits: " + error.getMessage()
            ));
          });
  }
}
