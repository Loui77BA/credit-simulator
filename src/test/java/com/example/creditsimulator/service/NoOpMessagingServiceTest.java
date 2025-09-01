package com.example.creditsimulator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.creditsimulator.model.Currency;
import com.example.creditsimulator.model.InterestRateScenario;
import com.example.creditsimulator.model.LoanSimulationRequest;
import com.example.creditsimulator.model.LoanSimulationResponse;

class NoOpMessagingServiceTest {

    private LoanSimulationService loanSimulationService;
    private NoOpMessagingService messagingService;
    private LoanSimulationRequest request;
    private LoanSimulationResponse expectedResponse;

    @BeforeEach
    void setup() {
        // Mock do serviço de simulação de empréstimo
        loanSimulationService = mock(LoanSimulationService.class);

        // Inicializa o serviço de mensageria com o mock
        messagingService = new NoOpMessagingService(loanSimulationService);

        // Cria uma requisição de exemplo
        request = new LoanSimulationRequest(
            BigDecimal.valueOf(1000),
            LocalDate.now().minusYears(30),
            12,
            InterestRateScenario.FIXED,
            Currency.BRL
        );

        // Cria uma resposta esperada
        expectedResponse = new LoanSimulationResponse(
            BigDecimal.valueOf(1030),
            BigDecimal.valueOf(85.83),
            BigDecimal.valueOf(30),
            BigDecimal.valueOf(0.03),
            Currency.BRL
        );

        // Configura o mock para retornar a resposta esperada
        when(loanSimulationService.simulateLoan(any(LoanSimulationRequest.class)))
            .thenReturn(expectedResponse);
    }

    @Test
    @DisplayName("Deve processar a requisição de simulação imediatamente")
    void shouldProcessSimulationRequestImmediately() {
        LoanSimulationResponse response = messagingService.processSimulationRequest(request);

        // Verifica se o serviço de simulação foi chamado
        verify(loanSimulationService, times(1)).simulateLoan(request);

        // Verifica se a resposta está correta
        assertNotNull(response);
        assertEquals(expectedResponse, response);
    }

    @Test
    @DisplayName("Método sendSimulationRequest deve chamar processSimulationRequest internamente")
    void sendSimulationRequestShouldCallProcessSimulationRequest() {
        messagingService.sendSimulationRequest(request);

        // Verifica se o serviço de simulação foi chamado uma vez
        verify(loanSimulationService, times(1)).simulateLoan(request);
    }
}
