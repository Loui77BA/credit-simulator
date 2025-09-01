package com.example.creditsimulator.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.example.creditsimulator.model.InterestRateScenario;

@Service
public class InterestRateProviderImpl implements InterestRateProvider {

    // Taxas fixas definidas por faixa etária
    private static final BigDecimal RATE_UP_TO_25 = BigDecimal.valueOf(0.05);
    private static final BigDecimal RATE_26_TO_40 = BigDecimal.valueOf(0.03);
    private static final BigDecimal RATE_41_TO_60 = BigDecimal.valueOf(0.02);
    private static final BigDecimal RATE_ABOVE_60 = BigDecimal.valueOf(0.04);

    // Ajuste arbitrário para taxas variáveis. Em um sistema real, estas taxas
    // viriam de um serviço de mercado ou indexador econômico.
    private static final BigDecimal VARIABLE_ADJUSTMENT = BigDecimal.valueOf(0.01);

    @Override
    public BigDecimal getAnnualInterestRate(int age, InterestRateScenario scenario) {
        BigDecimal baseRate;
        if (age <= 25) {
            baseRate = RATE_UP_TO_25;
        } else if (age <= 40) {
            baseRate = RATE_26_TO_40;
        } else if (age <= 60) {
            baseRate = RATE_41_TO_60;
        } else {
            baseRate = RATE_ABOVE_60;
        }

        if (scenario == InterestRateScenario.VARIABLE) {
            // Para o cenário variável, acrescentamos um ajuste fixo como exemplo
            return baseRate.add(VARIABLE_ADJUSTMENT).setScale(4, RoundingMode.HALF_UP);
        }
        return baseRate.setScale(4, RoundingMode.HALF_UP);
    }
}
