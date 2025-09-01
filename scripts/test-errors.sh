#!/bin/bash

# Script para testar validação de erros
echo "🛡️ Testando validação e tratamento de erros"
echo "=========================================="

# Função para testar um caso de erro
test_error_case() {
  local description=$1
  local payload=$2

  echo "🧪 Testando: $description"
  echo "📝 Payload: $payload"

  result=$(curl -s -X POST http://localhost:8080/api/simulations/single \
    -H "Content-Type: application/json" \
    -d "$payload")

  echo "📝 Resposta:"
  echo "$result" | jq '.'

  echo "-----------------------------------"
}

# Testar casos de erro
test_error_case "Valor negativo de empréstimo" '{
  "loanAmount": -1000.00,
  "dateOfBirth": "1990-01-01",
  "termMonths": 24,
  "interestRateScenario": "FIXED",
  "currency": "BRL"
}'

test_error_case "Moeda inválida" '{
  "loanAmount": 1000.00,
  "dateOfBirth": "1990-01-01",
  "termMonths": 24,
  "interestRateScenario": "FIXED",
  "currency": "GBP"
}'

test_error_case "Data de nascimento no futuro" '{
  "loanAmount": 1000.00,
  "dateOfBirth": "2030-01-01",
  "termMonths": 24,
  "interestRateScenario": "FIXED",
  "currency": "BRL"
}'

echo "✅ Testes de validação concluídos"
