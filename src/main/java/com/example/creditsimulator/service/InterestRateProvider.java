package com.example.creditsimulator.service;

import com.example.creditsimulator.model.InterestRateScenario;
import java.math.BigDecimal;

public interface InterestRateProvider {

    /**
     * Obtém a taxa de juros anual a partir da idade do cliente e do cenário de taxa.
     *
     * @param age    idade do cliente em anos completos
     * @param scenario tipo de cenário (fixo ou variável)
     * @return taxa de juros anual como fração (ex.: 0,05 para 5%)
     */
    BigDecimal getAnnualInterestRate(int age, InterestRateScenario scenario);
}