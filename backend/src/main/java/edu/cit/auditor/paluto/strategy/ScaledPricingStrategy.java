package edu.cit.auditor.paluto.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ScaledPricingStrategy implements PricingStrategy {
    @Override
    public int calculatePrepTime(int basePrepTime, int quantity) {
        if (quantity <= 1) return basePrepTime;
        // 100% for first set, 20% for each additional set
        double scaled = basePrepTime + (basePrepTime * 0.20 * (quantity - 1));
        return (int) Math.round(scaled);
    }

    @Override
    public BigDecimal calculateLaborTotal(BigDecimal hourlyRate, int minutes) {
        return hourlyRate.multiply(BigDecimal.valueOf(minutes))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }
}
