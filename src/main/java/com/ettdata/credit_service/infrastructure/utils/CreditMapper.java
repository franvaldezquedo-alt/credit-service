package com.ettdata.credit_service.infrastructure.utils;

import com.ettdata.credit_service.domain.model.Credit;
import com.ettdata.credit_service.domain.model.CreditStatus;
import com.ettdata.credit_service.infrastructure.entity.CreditEntity;
import com.ettdata.credit_service.infrastructure.model.CreditRequest;
import com.ettdata.credit_service.infrastructure.model.DisbursementRequest;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CreditMapper {

  /**
   * Convierte CreditEntity (Infraestructura) a Credit (Dominio)
   */
  public Credit toDomain(CreditEntity entity) {
    if (entity == null) {
      return null;
    }

    return Credit.builder()
          .id(entity.getId())
          .creditNumber(entity.getCreditNumber())
          .customerDocument(entity.getCustomerDocument())
          .type(entity.getType())
          .creditLimit(entity.getCreditLimit())
          .currentDebt(entity.getCurrentDebt())
          .availableCredit(entity.getAvailableCredit())
          .interestRate(entity.getInterestRate())
          .termMonths(entity.getTermMonths())
          .monthlyPayment(entity.getMonthlyPayment())
          .dueDate(entity.getDueDate())
          .hasOverdueDebt(entity.getHasOverdueDebt())
          .status(entity.getStatus())
          .createdAt(entity.getCreatedAt())
          .updatedAt(entity.getUpdatedAt())
          .build();
  }

  /**
   * Convierte Credit (Dominio) a CreditEntity (Infraestructura)
   */
  public CreditEntity toEntity(Credit domain) {
    if (domain == null) {
      return null;
    }

    return CreditEntity.builder()
          .id(domain.getId())
          .creditNumber(domain.getCreditNumber())
          .customerDocument(domain.getCustomerDocument())
          .type(domain.getType())
          .creditLimit(domain.getCreditLimit())
          .currentDebt(domain.getCurrentDebt())
          .availableCredit(domain.getAvailableCredit())
          .interestRate(domain.getInterestRate())
          .termMonths(domain.getTermMonths())
          .monthlyPayment(domain.getMonthlyPayment())
          .dueDate(domain.getDueDate())
          .hasOverdueDebt(domain.getHasOverdueDebt())
          .status(domain.getStatus())
          .createdAt(domain.getCreatedAt())
          .updatedAt(domain.getUpdatedAt())
          .build();
  }

  /**
   * Convierte CreditRequest a Credit (Dominio)
   * Inicializa valores por defecto para nuevo crédito
   */
  public Credit requestToDomain(CreditRequest request) {
    if (request == null) {
      return null;
    }

    LocalDate dueDate = calculateDueDate();
    BigDecimal monthlyPayment = calculateMonthlyPayment(
          request.getCreditLimit(),
          request.getInterestRate(),
          request.getTermMonths()
    );

    return Credit.builder()
          .id(UUID.randomUUID().toString())
          .creditNumber(generateCreditNumber(request.getCreditType()))
          .customerDocument(request.getCustomerDocument())
          .type(request.getCreditType())
          .creditLimit(request.getCreditLimit())
          .currentDebt(BigDecimal.ZERO)
          .availableCredit(request.getCreditLimit())
          .interestRate(request.getInterestRate())
          .termMonths(request.getTermMonths())
          .monthlyPayment(monthlyPayment)
          .dueDate(dueDate)
          .hasOverdueDebt(false)
          .status(CreditStatus.ACTIVE)
          .createdAt(LocalDateTime.now())
          .updatedAt(LocalDateTime.now())
          .build();
  }

  private Credit disbursementRequestToDomain(DisbursementRequest request, Credit existingCredit) {
    if (request == null || existingCredit == null) {
      return null;
    }

    BigDecimal disbursementAmount = request.getAmount();
    BigDecimal newAvailableCredit = existingCredit.getAvailableCredit().subtract(disbursementAmount);
    BigDecimal newCurrentDebt = existingCredit.getCurrentDebt().add(disbursementAmount);

    if (newAvailableCredit.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Insufficient available credit for this disbursement");
    }

    return Credit.builder()
          .id(existingCredit.getId())
          .creditNumber(existingCredit.getCreditNumber())
          .customerDocument(existingCredit.getCustomerDocument())
          .type(existingCredit.getType())
          .creditLimit(existingCredit.getCreditLimit())
          .currentDebt(newCurrentDebt)
          .availableCredit(newAvailableCredit)
          .interestRate(existingCredit.getInterestRate())
          .termMonths(existingCredit.getTermMonths())
          .monthlyPayment(existingCredit.getMonthlyPayment())
          .dueDate(existingCredit.getDueDate())
          .hasOverdueDebt(existingCredit.getHasOverdueDebt())
          .status(existingCredit.getStatus())
          .createdAt(existingCredit.getCreatedAt())
          .updatedAt(java.time.LocalDateTime.now())
          .build();
  }


  // ==================== HELPER METHODS ====================

  /**
   * Genera número de crédito único
   */
  private String generateCreditNumber(com.ettdata.credit_service.domain.model.CreditType type) {
    String prefix;
    switch (type) {
      case PERSONAL:
        prefix = "CRP";
        break;
      case BUSINESS:
        prefix = "CRB";
        break;
      case CREDIT_CARD:
        prefix = "CC";
        break;
      default:
        prefix = "CR";
    }

    return String.format("%s-%d-%04d",
          prefix,
          System.currentTimeMillis() % 1000000,
          (int)(Math.random() * 10000)
    );
  }

  /**
   * Calcula fecha de vencimiento (primer día del mes siguiente)
   */
  private LocalDate calculateDueDate() {
    return LocalDate.now().plusMonths(1).withDayOfMonth(1);
  }

  /**
   * Calcula cuota mensual usando amortización francesa
   * Fórmula: M = P * [r(1+r)^n] / [(1+r)^n - 1]
   */
  private BigDecimal calculateMonthlyPayment(
        BigDecimal principal,
        BigDecimal annualRate,
        Integer months) {

    if (principal == null || annualRate == null || months == null || months == 0) {
      return BigDecimal.ZERO;
    }

    // Sin interés
    if (annualRate.compareTo(BigDecimal.ZERO) == 0) {
      return principal.divide(BigDecimal.valueOf(months), 2, BigDecimal.ROUND_HALF_UP);
    }

    // Tasa mensual
    BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 6, BigDecimal.ROUND_HALF_UP);

    // Cálculo
    double r = monthlyRate.doubleValue();
    double p = principal.doubleValue();
    double power = Math.pow(1 + r, months);
    double payment = p * (r * power) / (power - 1);

    return BigDecimal.valueOf(payment).setScale(2, BigDecimal.ROUND_HALF_UP);
  }


}
