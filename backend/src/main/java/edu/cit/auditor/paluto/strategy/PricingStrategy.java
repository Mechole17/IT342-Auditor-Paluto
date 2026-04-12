package edu.cit.auditor.paluto.strategy;

import java.math.BigDecimal;

public interface PricingStrategy {
    double calculatePrepTime(double basePrepTime, int quantity);
    BigDecimal calculateLaborTotal(BigDecimal hourlyRate, double totalPrepTimeMinutes);
}
