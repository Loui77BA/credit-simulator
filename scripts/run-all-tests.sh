#!/bin/bash

# Script para testar todos os endpoints
echo "🚀 Testando todos os endpoints do simulador de crédito"
echo "===================================================="

# Definir cores para saída
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar disponibilidade da API
echo -e "${YELLOW}Verificando disponibilidade da API...${NC}"
if curl -s --head http://localhost:8080/ > /dev/null; then
  echo -e "${GREEN}✅ API está disponível${NC}"
else
  echo -e "❌ API não está respondendo. Verifique se o serviço está em execução."
  exit 1
fi

echo -e "\n${YELLOW}1. Executando teste de faixas etárias${NC}"
./scripts/test-age-rates.sh

echo -e "\n${YELLOW}2. Executando teste de comparação de taxas${NC}"
./scripts/compare-rates.sh

echo -e "\n${YELLOW}3. Executando teste de moedas${NC}"
./scripts/test-currencies.sh

echo -e "\n${YELLOW}4. Executando teste em lote${NC}"
./scripts/test-bulk.sh

echo -e "\n${YELLOW}5. Executando teste de erros${NC}"
./scripts/test-errors.sh

echo -e "\n${GREEN}✅ Todos os testes foram executados com sucesso!${NC}"
