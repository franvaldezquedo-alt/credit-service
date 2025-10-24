package com.ettdata.credit_service.infrastructure.utils;

import com.ettdata.credit_service.application.port.out.CreditRepositoryOutputPort;
import com.ettdata.credit_service.application.port.out.CustomerOutputPort;
import com.ettdata.credit_service.domain.error.CreditHasDebtException;
import com.ettdata.credit_service.domain.error.CreditNotFoundException;
import com.ettdata.credit_service.domain.error.CustomerNotFoundException;
import com.ettdata.credit_service.domain.error.CustomerServiceException;
import com.ettdata.credit_service.domain.error.DuplicateCreditException;
import com.ettdata.credit_service.domain.error.OverdueDebtException;
import com.ettdata.credit_service.domain.model.Credit;
import com.ettdata.credit_service.domain.model.CreditStatus;
import com.ettdata.credit_service.domain.model.CreditType;
import com.ettdata.credit_service.infrastructure.model.CreditRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CreditValidator {
  private final CustomerOutputPort customerOutputPort;
  private final CreditRepositoryOutputPort creditRepositoryOutputPort;

  public CreditValidator(CustomerOutputPort customerOutputPort,
                         CreditRepositoryOutputPort creditRepositoryOutputPort) {
    this.customerOutputPort = customerOutputPort;
    this.creditRepositoryOutputPort = creditRepositoryOutputPort;
  }

  /**
   * Ejecuta todas las validaciones de negocio para crear un crédito
   */
  public Mono<CreditRequest> validateCreditCreation(CreditRequest request) {
    log.debug("Starting business rules validation for credit type: {}", request.getCreditType());

    return Mono.just(request)
          .flatMap(this::validateCustomerExists)
          .flatMap(this::validateNoOverdueDebts)
          .flatMap(this::validateCreditTypeRestrictions)
          .doOnSuccess(r -> log.info("All business rules validated successfully"))
          .doOnError(ex -> log.error("Business rules validation failed: {}", ex.getMessage()));
  }

  /**
   * Valida que el cliente existe consultando el servicio de customers
   */
  public Mono<CreditRequest> validateCustomerExists(CreditRequest request) {
    log.debug("Validating customer exists: {}", request.getCustomerDocument());

    return customerOutputPort.getCustomerByDocument(request.getCustomerDocument())
          .flatMap(customerResponse -> {
            log.info("Customer found. Document: {}", request.getCustomerDocument());
            return Mono.just(request);
          })
          .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                "Customer not found with document: " + request.getCustomerDocument()
          )))
          .onErrorResume(CustomerNotFoundException.class, Mono::error)
          .onErrorResume(ex -> {
            log.error("Error validating customer {}: {}",
                  request.getCustomerDocument(), ex.getMessage());
            return Mono.error(new CustomerServiceException(
                  "Error consulting customer service: " + ex.getMessage()));
          });
  }

  /**
   * Valida que el cliente no tiene deudas vencidas
   */
  public Mono<CreditRequest> validateNoOverdueDebts(CreditRequest request) {
    log.debug("Validating no overdue debts for customer: {}", request.getCustomerDocument());

    return creditRepositoryOutputPort.findByDocumentNumber(request.getCustomerDocument())
          .filter(Credit::getHasOverdueDebt)
          .hasElements()
          .flatMap(hasOverdue -> {
            if (hasOverdue) {
              log.warn("Customer {} has overdue debts", request.getCustomerDocument());
              return Mono.error(new OverdueDebtException(
                    "Customer has overdue debts. Cannot acquire new credit."));
            }
            log.debug("Customer has no overdue debts");
            return Mono.just(request);
          });
  }

  /**
   * Valida restricciones por tipo de crédito:
   * - PERSONAL: Solo 1 crédito activo por cliente
   * - BUSINESS: Múltiples permitidos
   * - CREDIT_CARD: Solo 1 tarjeta activa por cliente
   */
  public Mono<CreditRequest> validateCreditTypeRestrictions(CreditRequest request) {
    log.debug("Validating credit type restrictions for: {}", request.getCreditType());

    switch (request.getCreditType()) {
      case PERSONAL:
        return validatePersonalCreditRestriction(request);
      case CREDIT_CARD:
        return validateCreditCardRestriction(request);
      case BUSINESS:
        // No hay restricciones para créditos empresariales
        log.debug("Business credit - no restrictions");
        return Mono.just(request);
      default:
        return Mono.just(request);
    }
  }

  /**
   * Valida que el cliente no tenga otro crédito personal activo
   */
  private Mono<CreditRequest> validatePersonalCreditRestriction(CreditRequest request) {
    log.debug("Checking if customer {} has another personal credit",
          request.getCustomerDocument());

    return creditRepositoryOutputPort.findByDocumentNumber(request.getCustomerDocument())
          .filter(credit -> credit.getType() == CreditType.PERSONAL)
          .filter(credit -> credit.getStatus() == CreditStatus.ACTIVE)
          .hasElements()
          .flatMap(hasPersonalCredit -> {
            if (hasPersonalCredit) {
              log.warn("Customer {} already has an active personal credit",
                    request.getCustomerDocument());
              return Mono.error(new DuplicateCreditException(
                    "Customer already has an active personal credit. Only one allowed."));
            }
            return Mono.just(request);
          });
  }

  /**
   * Valida que el cliente no tenga otra tarjeta de crédito activa
   */
  private Mono<CreditRequest> validateCreditCardRestriction(CreditRequest request) {
    log.debug("Checking if customer {} has another credit card",
          request.getCustomerDocument());

    return creditRepositoryOutputPort.findByDocumentNumber(request.getCustomerDocument())
          .filter(credit -> credit.getType() == CreditType.CREDIT_CARD)
          .filter(credit -> credit.getStatus() == CreditStatus.ACTIVE)
          .hasElements()
          .flatMap(hasCreditCard -> {
            if (hasCreditCard) {
              log.warn("Customer {} already has an active credit card",
                    request.getCustomerDocument());
              return Mono.error(new DuplicateCreditException(
                    "Customer already has an active credit card. Only one allowed."));
            }
            return Mono.just(request);
          });
  }

  /**
   * Valida que un crédito existe antes de eliminarlo
   */
  public Mono<String> validateCreditExistsForDeletion(String creditId) {
    log.debug("Validating credit exists for deletion: {}", creditId);

    return creditRepositoryOutputPort.findById(creditId)
          .map(Credit::getId)
          .switchIfEmpty(Mono.error(new CreditNotFoundException(
                "Credit not found with ID: " + creditId
          )))
          .doOnSuccess(id -> log.debug("Credit {} exists and can be deleted", id));
  }

  /**
   * Valida que un crédito puede ser eliminado (sin deuda pendiente)
   */
  public Mono<Credit> validateCreditCanBeDeleted(Credit credit) {
    log.debug("Validating credit {} can be deleted", credit.getId());

    if (credit.getCurrentDebt().compareTo(java.math.BigDecimal.ZERO) > 0) {
      log.warn("Credit {} has pending debt: {}", credit.getId(), credit.getCurrentDebt());
      return Mono.error(new CreditHasDebtException(
            "Cannot delete credit with pending debt: " + credit.getCurrentDebt()));
    }

    log.debug("Credit {} can be deleted", credit.getId());
    return Mono.just(credit);
  }
}
