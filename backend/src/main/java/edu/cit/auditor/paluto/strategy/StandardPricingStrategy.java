package edu.cit.auditor.paluto.strategy;

import java.math.BigDecimal;

public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public int calculatePrepTime(int basePrepTime, int quantity) {
        return basePrepTime;
    }

    @Override
    public BigDecimal calculateLaborTotal(BigDecimal hourlyRate, int minutes) {
        return hourlyRate.multiply(BigDecimal.valueOf(minutes))
                .divide(java.math.BigDecimal.valueOf(60), 4, java.math.RoundingMode.HALF_UP);
    }
}
