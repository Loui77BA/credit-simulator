#!/bin/bash

# Script para testar diferentes moedas
echo "🌎 Testando simulações com diferentes moedas"
echo "============================================"

# Função para testar uma moeda
test_currency() {
  local amount=$1
  local currency=$2
  local description=$3

  echo "🧪 Testando $description ($currency) - Valor: $amount"

  curl -s -X POST http://localhost:8080/api/simulations/single \
    -H "Content-Type: application/json" \
    -d "{
      \"loanAmount\": $amount,
      \"dateOfBirth\": \"1990-01-01\",
      \"termMonths\": 24,
      \"interestRateScenario\": \"FIXED\",
      \"currency\": \"$currency\"
    }" | jq '{moeda: .currency, valor_total: .totalPayableAmount, parcela: .monthlyInstallment}'

  echo "-----------------------------------"
}

# Testar cada moeda
test_currency "10000.00" "BRL" "Real Brasileiro"
test_currency "2000.00" "USD" "Dólar Americano"
test_currency "2000.00" "EUR" "Euro"

echo "✅ Testes de moeda concluídos"
