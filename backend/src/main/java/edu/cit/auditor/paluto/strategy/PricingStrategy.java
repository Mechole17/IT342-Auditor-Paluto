package edu.cit.auditor.paluto.strategy;

import java.math.BigDecimal;

public interface PricingStrategy {
    int calculatePrepTime(int basePrepTime, int quantity);
    BigDecimal calculateLaborTotal(BigDecimal hourlyRate, int totalPrepTimeMinutes);
}
