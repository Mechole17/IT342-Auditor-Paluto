package edu.cit.auditor.paluto.strategy;

import java.math.BigDecimal;

public class ScaledPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrepTime(double basePrepTime, int quantity) {
        // 1st set = 100%, extra sets = 20% each
        return basePrepTime + (basePrepTime * 0.20 * (quantity - 1));
    }

    @Override
    public BigDecimal calculateLaborTotal(BigDecimal hourlyRate, double minutes) {
        return hourlyRate.multiply(BigDecimal.valueOf(minutes))
                .divide(java.math.BigDecimal.valueOf(60), 4, java.math.RoundingMode.HALF_UP);
    }
}
