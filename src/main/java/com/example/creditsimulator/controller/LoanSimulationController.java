package com.example.creditsimulator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.creditsimulator.model.LoanSimulationRequest;
import com.example.creditsimulator.model.LoanSimulationResponse;
import com.example.creditsimulator.service.LoanSimulationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/simulations")
public class LoanSimulationController {

    private final LoanSimulationService loanSimulationService;

    @Autowired
    public LoanSimulationController(LoanSimulationService loanSimulationService) {
        this.loanSimulationService = loanSimulationService;
    }

    /**
     * Endpoint para simulação única.
     *
     * @param request objeto contendo dados da simulação
     * @return resposta com os valores calculados
     */
    @PostMapping("/single")
    public ResponseEntity<LoanSimulationResponse> simulateSingle(@Valid @RequestBody LoanSimulationRequest request) {
        LoanSimulationResponse response = loanSimulationService.simulateLoan(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint para simulação em lote. Esse endpoint aceita uma lista de
     * requisições e retorna a lista de resultados correspondente. As
     * simulações são processadas de forma concorrente para suportar alta
     * volumetria.
     *
     * @param requests lista de requisições
     * @return lista de respostas
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<LoanSimulationResponse>> simulateBulk(@Valid @RequestBody List<@Valid LoanSimulationRequest> requests) {
        List<LoanSimulationResponse> responses = loanSimulationService.simulateLoans(requests);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
