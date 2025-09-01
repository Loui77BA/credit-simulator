package com.example.creditsimulator.service;

import java.math.BigDecimal;

import com.example.creditsimulator.model.Currency;

public interface CurrencyConversionService {

    /**
     * Converte um valor de uma moeda de origem para uma moeda de destino.
     *
     * @param amount valor a ser convertido
     * @param from   moeda de origem
     * @param to     moeda de destino
     * @return valor convertido na moeda de destino
     */
    BigDecimal convert(BigDecimal amount, Currency from, Currency to);

    /**
     * Obtém a taxa de conversão de uma moeda de origem para uma de destino.
     *
     * @param from moeda de origem
     * @param to   moeda de destino
     * @return taxa multiplicadora (ex.: 5,0 para converter de USD para BRL a 5,0 R$/US$)
     */
    BigDecimal getConversionRate(Currency from, Currency to);
}
