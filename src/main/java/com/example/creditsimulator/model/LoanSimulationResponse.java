package com.example.creditsimulator.model;

import java.math.BigDecimal;

public class LoanSimulationResponse {

    /** Valor total a ser pago ao final do prazo. */
    private BigDecimal totalPayableAmount;
    /** Valor das parcelas mensais. */
    private BigDecimal monthlyInstallment;
    /** Total de juros pagos ao longo do contrato. */
    private BigDecimal totalInterestPaid;
    /** Taxa de juros anual aplicada na simulação. */
    private BigDecimal annualInterestRate;
    /** Moeda utilizada na simulação. */
    private Currency currency;

    public LoanSimulationResponse() {
        // Construtor vazio para frameworks de serialização
    }

    public LoanSimulationResponse(BigDecimal totalPayableAmount, BigDecimal monthlyInstallment,
                                  BigDecimal totalInterestPaid, BigDecimal annualInterestRate, Currency currency) {
        this.totalPayableAmount = totalPayableAmount;
        this.monthlyInstallment = monthlyInstallment;
        this.totalInterestPaid = totalInterestPaid;
        this.annualInterestRate = annualInterestRate;
        this.currency = currency;
    }

    public BigDecimal getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public void setTotalPayableAmount(BigDecimal totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }

    public BigDecimal getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public void setMonthlyInstallment(BigDecimal monthlyInstallment) {
        this.monthlyInstallment = monthlyInstallment;
    }

    public BigDecimal getTotalInterestPaid() {
        return totalInterestPaid;
    }

    public void setTotalInterestPaid(BigDecimal totalInterestPaid) {
        this.totalInterestPaid = totalInterestPaid;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(BigDecimal annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
