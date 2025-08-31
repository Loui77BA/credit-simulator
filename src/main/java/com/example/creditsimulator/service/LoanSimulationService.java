package com.example.creditsimulator.service;

import com.example.creditsimulator.model.Currency;
import com.example.creditsimulator.model.InterestRateScenario;
import com.example.creditsimulator.model.LoanSimulationRequest;
import com.example.creditsimulator.model.LoanSimulationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class LoanSimulationService {

    private final InterestRateProvider interestRateProvider;
    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public LoanSimulationService(InterestRateProvider interestRateProvider,
                                 CurrencyConversionService currencyConversionService) {
        this.interestRateProvider = interestRateProvider;
        this.currencyConversionService = currencyConversionService;
    }

    /**
     * Realiza a simulação de um empréstimo individual.
     *
     * @param request requisição contendo valores e parâmetros de simulação
     * @return resposta com detalhamento do empréstimo
     */
    public LoanSimulationResponse simulateLoan(LoanSimulationRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        // Determina idade a partir da data de nascimento
        int age = calculateAge(request.getDateOfBirth(), LocalDate.now());
        BigDecimal annualRate = interestRateProvider.getAnnualInterestRate(age, request.getInterestRateScenario());

        // Converte o valor do empréstimo para BRL para realizar o cálculo padronizado
        BigDecimal principalInBRL = currencyConversionService.convert(
                request.getLoanAmount(), request.getCurrency(), Currency.BRL);

        // Calcula a taxa de juros mensal: divide a taxa anual por 12
        MathContext mc = new MathContext(16, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), mc);

        int n = request.getTermMonths();
        // Calcula (1 + r)^n. Para evitar potências negativas, utiliza-se a inversão posteriormente.
        BigDecimal onePlusRatePow = BigDecimal.ONE.add(monthlyRate).pow(n, mc);
        // PMT = (PV * r) / (1 - (1 + r)^(-n)) = (PV * r) / (1 - 1 / ((1 + r)^n))
        BigDecimal numerator = principalInBRL.multiply(monthlyRate, mc);
        BigDecimal denominator = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(onePlusRatePow, mc), mc);
        BigDecimal monthlyInstallmentBRL = numerator.divide(denominator, mc);

        // Valor total a ser pago e total de juros
        BigDecimal totalPayableBRL = monthlyInstallmentBRL.multiply(BigDecimal.valueOf(n), mc);
        BigDecimal totalInterestBRL = totalPayableBRL.subtract(principalInBRL, mc);

        // Converte valores de volta para a moeda de origem
        BigDecimal monthlyInstallment = currencyConversionService.convert(
                monthlyInstallmentBRL, Currency.BRL, request.getCurrency());
        BigDecimal totalPayable = currencyConversionService.convert(
                totalPayableBRL, Currency.BRL, request.getCurrency());
        BigDecimal totalInterest = currencyConversionService.convert(
                totalInterestBRL, Currency.BRL, request.getCurrency());

        // Ajusta escala final para valores monetários (duas casas decimais)
        monthlyInstallment = monthlyInstallment.setScale(2, RoundingMode.HALF_UP);
        totalPayable = totalPayable.setScale(2, RoundingMode.HALF_UP);
        totalInterest = totalInterest.setScale(2, RoundingMode.HALF_UP);

        return new LoanSimulationResponse(totalPayable, monthlyInstallment, totalInterest, annualRate, request.getCurrency());
    }

    public List<LoanSimulationResponse> simulateLoans(List<LoanSimulationRequest> requests) {
        Objects.requireNonNull(requests, "requests must not be null");
        return requests.parallelStream()
                .map(this::simulateLoan)
                .collect(Collectors.toList());
    }

    /**
     * Calcula a idade em anos completos de uma pessoa com base na data de
     * nascimento. Se a data de nascimento for nula, a idade padrão é zero.
     *
     * @param birthDate data de nascimento
     * @param now       data atual
     * @return idade em anos
     */
    private int calculateAge(LocalDate birthDate, LocalDate now) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, now).getYears();
    }
}