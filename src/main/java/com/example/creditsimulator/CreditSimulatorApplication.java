package com.example.creditsimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CreditSimulatorApplication {

    /**
     * Método de entrada da aplicação. Usa o {@link SpringApplication} para iniciar
     * o contexto Spring.
     *
     * @param args argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        SpringApplication.run(CreditSimulatorApplication.class, args);
    }
}
