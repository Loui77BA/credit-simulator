#!/bin/bash

# Função para testar uma faixa etária
test_age_group() {
  local birth_year=$1
  local expected_rate=$2
  local age_group=$3

  echo "🧪 Testando faixa etária: $age_group (nascido em $birth_year, taxa esperada: $expected_rate%)"

  response=$(curl -s -X POST http://localhost:8080/api/simulations/single \
    -H "Content-Type: application/json" \
    -d "{
      \"loanAmount\": 10000.00,
      \"dateOfBirth\": \"$birth_year-01-01\",
      \"termMonths\": 24,
      \"interestRateScenario\": \"FIXED\",
      \"currency\": \"BRL\"
    }")

  actual_rate=$(echo $response | jq '.annualInterestRate')
  expected_rate_decimal=$(echo "$expected_rate/100" | bc -l)

  # Comparar com pequena margem de erro para evitar problemas de arredondamento
  if echo "$actual_rate $expected_rate_decimal" | awk '{if (($1 >= $2-0.0001) && ($1 <= $2+0.0001)) exit 0; else exit 1}'; then
    echo "✅ Taxa correta: $actual_rate"
    echo "💰 Parcela mensal: $(echo $response | jq '.monthlyInstallment')"
    echo "💵 Total a pagar: $(echo $response | jq '.totalPayableAmount')"
  else
    echo "❌ Taxa incorreta! Esperado: $expected_rate_decimal, Obtido: $actual_rate"
  fi
  echo "-----------------------------------"
}

# Testar cada faixa etária
test_age_group "2000" 5 "Até 25 anos"
test_age_group "1990" 3 "Entre 26-40 anos"
test_age_group "1970" 2 "Entre 41-60 anos"
test_age_group "1950" 4 "Acima de 60 anos"
