package com.example.creditsimulator.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.example.creditsimulator.model.Currency;

@Service
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    // Taxas de conversão base: 1 unidade da moeda chave para BRL
    private static final BigDecimal USD_TO_BRL = BigDecimal.valueOf(5.0);
    private static final BigDecimal EUR_TO_BRL = BigDecimal.valueOf(6.0);

    @Override
    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        if (from == to) {
            return amount;
        }
        BigDecimal rate = getConversionRate(from, to);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getConversionRate(Currency from, Currency to) {
        if (from == to) {
            return BigDecimal.ONE;
        }

        // Primeiro converte para BRL, depois converte para a moeda de destino.
        BigDecimal amountInBRL;
        switch (from) {
            case USD:
                amountInBRL = USD_TO_BRL;
                break;
            case EUR:
                amountInBRL = EUR_TO_BRL;
                break;
            case BRL:
            default:
                amountInBRL = BigDecimal.ONE;
                break;
        }

        // Conversão de BRL para a moeda destino
        BigDecimal rateFromBRLToTarget;
        switch (to) {
            case USD:
                rateFromBRLToTarget = BigDecimal.ONE.divide(USD_TO_BRL, 8, RoundingMode.HALF_UP);
                break;
            case EUR:
                rateFromBRLToTarget = BigDecimal.ONE.divide(EUR_TO_BRL, 8, RoundingMode.HALF_UP);
                break;
            case BRL:
            default:
                rateFromBRLToTarget = BigDecimal.ONE;
                break;
        }

        return amountInBRL.multiply(rateFromBRLToTarget).setScale(8, RoundingMode.HALF_UP);
    }
}
