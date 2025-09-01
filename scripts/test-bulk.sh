#!/bin/bash

# Script para testar simulações em lote
echo "📊 Testando simulação em lote e análise estatística"
echo "=================================================="

# Criar arquivo de teste em lote
cat > /tmp/bulk-test.json << 'EOF'
[
  {
    "loanAmount": 5000.00,
    "dateOfBirth": "1985-02-10",
    "termMonths": 12,
    "interestRateScenario": "FIXED",
    "currency": "USD"
  },
  {
    "loanAmount": 15000.00,
    "dateOfBirth": "1975-11-30",
    "termMonths": 36,
    "interestRateScenario": "VARIABLE",
    "currency": "EUR"
  },
  {
    "loanAmount": 20000.00,
    "dateOfBirth": "2000-05-15",
    "termMonths": 24,
    "interestRateScenario": "FIXED",
    "currency": "BRL"
  },
  {
    "loanAmount": 7500.00,
    "dateOfBirth": "1950-08-22",
    "termMonths": 48,
    "interestRateScenario": "VARIABLE",
    "currency": "BRL"
  }
]
EOF

echo "🧪 Enviando requisição em lote..."
curl -s -X POST http://localhost:8080/api/simulations/bulk \
  -H "Content-Type: application/json" \
  --data-binary @/tmp/bulk-test.json > /tmp/bulk-results.json

echo "📝 Resultados da simulação em lote:"
jq '.' /tmp/bulk-results.json

echo "📊 Análise estatística:"

# Soma dos valores emprestados separadamente para evitar o problema anterior
echo "Valores emprestados:"
jq '[.[].loanAmount] | add' /tmp/bulk-test.json

# Estatísticas completas
jq '
{
  "total_emprestimos": length,
  "soma_valores_pagos": [.[].totalPayableAmount] | add,
  "media_taxas_juros": (([.[].annualInterestRate] | add) / length * 100),
  "maior_valor_parcela": [.[].monthlyInstallment] | max,
  "menor_valor_parcela": [.[].monthlyInstallment] | min
}
' /tmp/bulk-results.json

echo "✅ Testes em lote concluídos"
