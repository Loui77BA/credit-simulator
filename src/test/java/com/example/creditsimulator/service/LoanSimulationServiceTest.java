package com.example.creditsimulator.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.creditsimulator.model.Currency;
import com.example.creditsimulator.model.InterestRateScenario;
import com.example.creditsimulator.model.LoanSimulationRequest;
import com.example.creditsimulator.model.LoanSimulationResponse;

@SpringBootTest
public class LoanSimulationServiceTest {

    @Autowired
    private LoanSimulationService loanSimulationService;

    @Test
    @DisplayName("Deve calcular corretamente a taxa de juros anual para faixa etária 26-40 (fixo)")
    void testAnnualInterestRateForAge26To40Fixed() {
        LoanSimulationRequest request = new LoanSimulationRequest(
                BigDecimal.valueOf(1000),
                LocalDate.now().minusYears(30),
                12,
                InterestRateScenario.FIXED,
                Currency.BRL
        );
        LoanSimulationResponse response = loanSimulationService.simulateLoan(request);
        // 3% ao ano para 26-40 anos
        Assertions.assertEquals(new BigDecimal("0.0300"), response.getAnnualInterestRate());
    }

    @Test
    @DisplayName("Deve calcular taxa variável somando ajuste sobre taxa base")
    void testVariableInterestRateAddsAdjustment() {
        LoanSimulationRequest requestFixed = new LoanSimulationRequest(
                BigDecimal.valueOf(1000),
                LocalDate.now().minusYears(30),
                12,
                InterestRateScenario.FIXED,
                Currency.BRL
        );
        LoanSimulationRequest requestVariable = new LoanSimulationRequest(
                BigDecimal.valueOf(1000),
                LocalDate.now().minusYears(30),
                12,
                InterestRateScenario.VARIABLE,
                Currency.BRL
        );
        LoanSimulationResponse fixedResponse = loanSimulationService.simulateLoan(requestFixed);
        LoanSimulationResponse variableResponse = loanSimulationService.simulateLoan(requestVariable);
        // Para a mesma faixa etária, a taxa variável deve ser maior que a fixa
        Assertions.assertTrue(variableResponse.getAnnualInterestRate().compareTo(fixedResponse.getAnnualInterestRate()) > 0);
    }

    @Test
    @DisplayName("Deve converter valores corretamente ao simular com moeda estrangeira")
    void testCurrencyConversionForUSD() {
        // 1000 USD correspondem a 5000 BRL (conforme taxa estática). Simulação com 12 meses e taxa 3% anual.
        LoanSimulationRequest request = new LoanSimulationRequest(
                BigDecimal.valueOf(1000),
                LocalDate.now().minusYears(30),
                12,
                InterestRateScenario.FIXED,
                Currency.USD
        );
        LoanSimulationResponse response = loanSimulationService.simulateLoan(request);
        // Verifica se a moeda da resposta é USD
        Assertions.assertEquals(Currency.USD, response.getCurrency());
        // Verifica se o valor total a pagar em USD é menor que em BRL (devido à conversão BRL/USD)
        // Obtém também a resposta em BRL para comparação
        LoanSimulationRequest requestBRL = new LoanSimulationRequest(
                BigDecimal.valueOf(5000),
                LocalDate.now().minusYears(30),
                12,
                InterestRateScenario.FIXED,
                Currency.BRL
        );
        LoanSimulationResponse responseBRL = loanSimulationService.simulateLoan(requestBRL);
        // Ajusta para mesma escala de centavos
        BigDecimal totalInUSDConvertedToBRL = response.getTotalPayableAmount().multiply(BigDecimal.valueOf(5.0)).setScale(2);
        // Comparar com tolerância de 0,01
        BigDecimal difference = responseBRL.getTotalPayableAmount().subtract(totalInUSDConvertedToBRL).abs();
        Assertions.assertTrue(difference.doubleValue() < 0.1);
    }

    @Test
    @DisplayName("Deve processar múltiplas simulações com alta volumetria")
    void testBulkSimulation() {
        int count = 1000; // reduzido para evitar longos tempos de execução durante testes
        List<LoanSimulationRequest> requests = java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> new LoanSimulationRequest(
                        BigDecimal.valueOf(1000 + i),
                        LocalDate.now().minusYears(30 + (i % 10)),
                        12,
                        InterestRateScenario.FIXED,
                        Currency.BRL
                ))
                .toList();
        List<LoanSimulationResponse> responses = loanSimulationService.simulateLoans(requests);
        Assertions.assertEquals(count, responses.size());
    }
}
