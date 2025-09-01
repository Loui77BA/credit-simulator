package com.example.creditsimulator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.creditsimulator.model.InterestRateScenario;

class InterestRateProviderImplTest {

    private final InterestRateProviderImpl interestRateProvider = new InterestRateProviderImpl();

    @ParameterizedTest
    @DisplayName("Deve retornar taxa fixa correta para diferentes faixas etárias")
    @CsvSource({
        "20, 0.0500", // até 25 anos
        "25, 0.0500", // até 25 anos (limite)
        "26, 0.0300", // 26-40 anos
        "40, 0.0300", // 26-40 anos (limite)
        "41, 0.0200", // 41-60 anos
        "60, 0.0200", // 41-60 anos (limite)
        "61, 0.0400", // acima de 60 anos
        "75, 0.0400"  // acima de 60 anos
    })
    void shouldReturnCorrectFixedRateForAgeGroup(int age, String expectedRate) {
        BigDecimal rate = interestRateProvider.getAnnualInterestRate(age, InterestRateScenario.FIXED);
        assertEquals(new BigDecimal(expectedRate), rate);
    }

    @Test
    @DisplayName("Deve adicionar ajuste à taxa fixa para cenário variável")
    void shouldAddAdjustmentForVariableRate() {
        // Testa para cada faixa etária

        // Faixa até 25 anos
        BigDecimal fixedRateUnder25 = interestRateProvider.getAnnualInterestRate(20, InterestRateScenario.FIXED);
        BigDecimal varRateUnder25 = interestRateProvider.getAnnualInterestRate(20, InterestRateScenario.VARIABLE);
        assertEquals(0, fixedRateUnder25.add(new BigDecimal("0.01")).compareTo(varRateUnder25),
            "Taxa variável deve ser taxa fixa + 0.01 para idade até 25 anos");

        // Faixa 26-40 anos
        BigDecimal fixedRateMid = interestRateProvider.getAnnualInterestRate(30, InterestRateScenario.FIXED);
        BigDecimal varRateMid = interestRateProvider.getAnnualInterestRate(30, InterestRateScenario.VARIABLE);
        assertEquals(0, fixedRateMid.add(new BigDecimal("0.01")).compareTo(varRateMid),
            "Taxa variável deve ser taxa fixa + 0.01 para idade entre 26-40 anos");

        // Faixa 41-60 anos
        BigDecimal fixedRateOlder = interestRateProvider.getAnnualInterestRate(50, InterestRateScenario.FIXED);
        BigDecimal varRateOlder = interestRateProvider.getAnnualInterestRate(50, InterestRateScenario.VARIABLE);
        assertEquals(0, fixedRateOlder.add(new BigDecimal("0.01")).compareTo(varRateOlder),
            "Taxa variável deve ser taxa fixa + 0.01 para idade entre 41-60 anos");

        // Faixa acima de 60 anos
        BigDecimal fixedRateSenior = interestRateProvider.getAnnualInterestRate(70, InterestRateScenario.FIXED);
        BigDecimal varRateSenior = interestRateProvider.getAnnualInterestRate(70, InterestRateScenario.VARIABLE);
        assertEquals(0, fixedRateSenior.add(new BigDecimal("0.01")).compareTo(varRateSenior),
            "Taxa variável deve ser taxa fixa + 0.01 para idade acima de 60 anos");
    }

    @Test
    @DisplayName("Deve manter escala decimal correta")
    void shouldMaintainCorrectScale() {
        BigDecimal rate = interestRateProvider.getAnnualInterestRate(30, InterestRateScenario.FIXED);
        assertEquals(4, rate.scale(), "A escala decimal deve ser 4 para maior precisão");
    }
}
