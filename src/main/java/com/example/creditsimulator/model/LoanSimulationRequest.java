package com.example.creditsimulator.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public class LoanSimulationRequest {

    /** Valor solicitado para empréstimo, em unidade da moeda especificada. */
    @NotNull(message = "O valor do empréstimo é obrigatório")
    @Min(value = 1, message = "O valor do empréstimo deve ser maior que zero")
    private BigDecimal loanAmount;

    /** Data de nascimento do cliente. Utilizada para determinar a faixa etária e a taxa de juros. */
    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve estar no passado")
    private LocalDate dateOfBirth;

    /** Prazo de pagamento em meses. */
    @NotNull(message = "O prazo em meses é obrigatório")
    @Min(value = 1, message = "O prazo deve ser de pelo menos 1 mês")
    private Integer termMonths;

    /** Cenário de taxa de juros a ser aplicado: FIXED ou VARIABLE. */
    @NotNull(message = "O cenário de taxa de juros é obrigatório")
    private InterestRateScenario interestRateScenario;

    /** Moeda em que o empréstimo é realizado. */
    @NotNull(message = "A moeda é obrigatória")
    private Currency currency;

    public LoanSimulationRequest() {
        // Construtor vazio para frameworks de serialização
    }

    public LoanSimulationRequest(BigDecimal loanAmount, LocalDate dateOfBirth, Integer termMonths,
                                 InterestRateScenario interestRateScenario, Currency currency) {
        this.loanAmount = loanAmount;
        this.dateOfBirth = dateOfBirth;
        this.termMonths = termMonths;
        this.interestRateScenario = interestRateScenario;
        this.currency = currency;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public InterestRateScenario getInterestRateScenario() {
        return interestRateScenario;
    }

    public void setInterestRateScenario(InterestRateScenario interestRateScenario) {
        this.interestRateScenario = interestRateScenario;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
