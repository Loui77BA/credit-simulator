package com.example.creditsimulator.service;

import com.example.creditsimulator.model.LoanSimulationRequest;
import com.example.creditsimulator.model.LoanSimulationResponse;

public interface MessagingService {

    void sendSimulationRequest(LoanSimulationRequest request);

    LoanSimulationResponse processSimulationRequest(LoanSimulationRequest request);
}