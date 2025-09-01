package com.example.creditsimulator.service;

import com.example.creditsimulator.model.LoanSimulationRequest;
import com.example.creditsimulator.model.LoanSimulationResponse;

public interface MessagingService {

    /**
     * Enfileira uma solicitação de simulação de empréstimo para processamento
     * assíncrono. Este método deve enviar a mensagem à fila e retornar
     * imediatamente.
     *
     * @param request requisição de simulação
     */
    void sendSimulationRequest(LoanSimulationRequest request);

    /**
     * Processa uma solicitação de simulação de forma síncrona, retornando
     * a resposta imediata. Em uma implementação assíncrona, este método
     * seria responsável por consumir a mensagem da fila, processar e
     * publicar o resultado em um tópico de resposta.
     *
     * @param request requisição de simulação
     * @return resultado da simulação
     */
    LoanSimulationResponse processSimulationRequest(LoanSimulationRequest request);
}
