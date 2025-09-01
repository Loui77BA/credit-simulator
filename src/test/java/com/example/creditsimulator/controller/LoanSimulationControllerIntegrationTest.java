package com.example.creditsimulator.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.creditsimulator.model.Currency;
import com.example.creditsimulator.model.InterestRateScenario;
import com.example.creditsimulator.model.LoanSimulationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class LoanSimulationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/simulations/single deve retornar resposta válida")
    void testSimulateSingle() throws Exception {
        LoanSimulationRequest request = new LoanSimulationRequest(
                BigDecimal.valueOf(1000),
                LocalDate.now().minusYears(35),
                12,
                InterestRateScenario.FIXED,
                Currency.BRL);
        String json = objectMapper.writeValueAsString(request);
        MvcResult result = mockMvc.perform(post("/api/simulations/single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("totalPayableAmount");
        assertThat(responseBody).contains("monthlyInstallment");
        assertThat(responseBody).contains("totalInterestPaid");
    }

    @Test
    @DisplayName("POST /api/simulations/bulk deve retornar lista de respostas")
    void testSimulateBulk() throws Exception {
        LoanSimulationRequest request1 = new LoanSimulationRequest(
                BigDecimal.valueOf(1000),
                LocalDate.now().minusYears(35),
                12,
                InterestRateScenario.FIXED,
                Currency.BRL);
        LoanSimulationRequest request2 = new LoanSimulationRequest(
                BigDecimal.valueOf(2000),
                LocalDate.now().minusYears(45),
                24,
                InterestRateScenario.VARIABLE,
                Currency.USD);
        String json = objectMapper.writeValueAsString(Arrays.asList(request1, request2));
        MvcResult result = mockMvc.perform(post("/api/simulations/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        // Deve iniciar com [ e terminar com ] por ser uma lista JSON
        assertThat(responseBody).startsWith("[");
        assertThat(responseBody).endsWith("]");
    }
}
