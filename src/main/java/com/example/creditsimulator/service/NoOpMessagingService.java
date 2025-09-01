package com.example.creditsimulator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.creditsimulator.model.LoanSimulationRequest;
import com.example.creditsimulator.model.LoanSimulationResponse;

@Service
@Primary
public class NoOpMessagingService implements MessagingService {

    private final LoanSimulationService loanSimulationService;

    @Autowired
    public NoOpMessagingService(LoanSimulationService loanSimulationService) {
        this.loanSimulationService = loanSimulationService;
    }

    @Override
    public void sendSimulationRequest(LoanSimulationRequest request) {
        // No-op: em um cenário real, enviaríamos a mensagem para uma fila.
        // Aqui processamos imediatamente para simplificação.
        processSimulationRequest(request);
    }

    @Override
    public LoanSimulationResponse processSimulationRequest(LoanSimulationRequest request) {
        return loanSimulationService.simulateLoan(request);
    }
}
