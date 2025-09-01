#!/bin/bash

echo "🔄 Comparando cenários de taxa FIXA vs VARIÁVEL"

# Testar taxa fixa
fixed_response=$(curl -s -X POST http://localhost:8080/api/simulations/single \
  -H "Content-Type: application/json" \
  -d '{
    "loanAmount": 10000.00,
    "dateOfBirth": "1990-01-01",
    "termMonths": 24,
    "interestRateScenario": "FIXED",
    "currency": "BRL"
  }')

# Testar taxa variável
variable_response=$(curl -s -X POST http://localhost:8080/api/simulations/single \
  -H "Content-Type: application/json" \
  -d '{
    "loanAmount": 10000.00,
    "dateOfBirth": "1990-01-01",
    "termMonths": 24,
    "interestRateScenario": "VARIABLE",
    "currency": "BRL"
  }')

# Extrair taxas
fixed_rate=$(echo $fixed_response | jq '.annualInterestRate')
variable_rate=$(echo $variable_response | jq '.annualInterestRate')

# Extrair valores
fixed_total=$(echo $fixed_response | jq '.totalPayableAmount')
variable_total=$(echo $variable_response | jq '.totalPayableAmount')

fixed_monthly=$(echo $fixed_response | jq '.monthlyInstallment')
variable_monthly=$(echo $variable_response | jq '.monthlyInstallment')

echo "📊 COMPARAÇÃO DE TAXAS"
echo "Taxa fixa: $fixed_rate (${fixed_rate/0./}%)"
echo "Taxa variável: $variable_rate (${variable_rate/0./}%)"
echo ""
echo "📊 COMPARAÇÃO DE VALORES"
echo "Total (fixa): R$ $fixed_total"
echo "Total (variável): R$ $variable_total"
echo "Diferença: R$ $(echo "$variable_total - $fixed_total" | bc)"
echo ""
echo "Parcela (fixa): R$ $fixed_monthly"
echo "Parcela (variável): R$ $variable_monthly"
echo "Diferença: R$ $(echo "$variable_monthly - $fixed_monthly" | bc)"
