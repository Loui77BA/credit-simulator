package com.example.creditsimulator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.creditsimulator.model.Currency;

class CurrencyConversionServiceImplTest {

    private final CurrencyConversionServiceImpl conversionService = new CurrencyConversionServiceImpl();

    @Test
    @DisplayName("Deve retornar a mesma quantia quando origem e destino são a mesma moeda")
    void shouldReturnSameAmountWhenSourceAndDestinationAreTheSame() {
        BigDecimal amount = BigDecimal.valueOf(1000.50);

        // BRL para BRL
        BigDecimal convertedBRL = conversionService.convert(amount, Currency.BRL, Currency.BRL);
        assertEquals(amount, convertedBRL);

        // USD para USD
        BigDecimal convertedUSD = conversionService.convert(amount, Currency.USD, Currency.USD);
        assertEquals(amount, convertedUSD);

        // EUR para EUR
        BigDecimal convertedEUR = conversionService.convert(amount, Currency.EUR, Currency.EUR);
        assertEquals(amount, convertedEUR);
    }

    @Test
    @DisplayName("Deve converter corretamente moedas diretas sem conversão cruzada")
    void shouldConvertDirectCurrenciesCorrectly() {
        // BRL para USD (1000 BRL = 200 USD com taxa 5.0)
        assertEquals(new BigDecimal("200.00"),
            conversionService.convert(new BigDecimal("1000.00"), Currency.BRL, Currency.USD));

        // BRL para EUR (1200 BRL = 200 EUR com taxa 6.0)
        assertEquals(new BigDecimal("200.00"),
            conversionService.convert(new BigDecimal("1200.00"), Currency.BRL, Currency.EUR));

        // USD para BRL (200 USD = 1000 BRL com taxa 5.0)
        assertEquals(new BigDecimal("1000.00"),
            conversionService.convert(new BigDecimal("200.00"), Currency.USD, Currency.BRL));

        // EUR para BRL (200 EUR = 1200 BRL com taxa 6.0)
        assertEquals(new BigDecimal("1200.00"),
            conversionService.convert(new BigDecimal("200.00"), Currency.EUR, Currency.BRL));
    }

    @Test
    @DisplayName("Deve converter corretamente moedas com conversão cruzada")
    void shouldConvertCrossCurrenciesCorrectly() {
        // USD para EUR:
        // 500 USD = 2500 BRL (500 * 5)
        // 2500 BRL = 416.67 EUR (2500 / 6)
        BigDecimal usdToEur = conversionService.convert(
            new BigDecimal("500.00"), Currency.USD, Currency.EUR);
        assertEquals(new BigDecimal("416.67"), usdToEur);

        // EUR para USD:
        // 600 EUR = 3600 BRL (600 * 6)
        // 3600 BRL = 720 USD (3600 / 5)
        BigDecimal eurToUsd = conversionService.convert(
            new BigDecimal("600.00"), Currency.EUR, Currency.USD);
        assertEquals(new BigDecimal("720.00"), eurToUsd);
    }    @Test
    @DisplayName("Deve fornecer taxas de conversão consistentes")
    void shouldProvideConsistentConversionRates() {
        // Taxa BRL -> USD deve ser o inverso de USD -> BRL
        BigDecimal brlToUsd = conversionService.getConversionRate(Currency.BRL, Currency.USD);
        BigDecimal usdToBrl = conversionService.getConversionRate(Currency.USD, Currency.BRL);
        BigDecimal product = brlToUsd.multiply(usdToBrl);

        // O produto das taxas inversas deve ser aproximadamente 1
        assertTrue(
            BigDecimal.ONE.subtract(product).abs().compareTo(new BigDecimal("0.0001")) < 0,
            "O produto das taxas inversas deve ser aproximadamente 1"
        );
    }

    @Test
    @DisplayName("Deve manter a escala decimal correta no resultado")
    void shouldMaintainCorrectScale() {
        BigDecimal amount = new BigDecimal("1000.505");
        BigDecimal converted = conversionService.convert(amount, Currency.USD, Currency.BRL);
        assertEquals(2, converted.scale(), "A escala decimal da conversão deve ser 2");
    }
}
