# Makefile do Projeto

# Variáveis
APP_NAME=credit-simulator
JAR_FILE=target/$(APP_NAME)-0.0.1-SNAPSHOT.jar
PORT=8080

# Cores para saída
GREEN=\033[0;32m
YELLOW=\033[1;33m
RED=\033[0;31m
NC=\033[0m

# Targets
.PHONY: help clean build run test docker-build docker-run docker-stop test-age-rates test-compare-rates test-currencies test-bulk test-errors test-all-api default

help:
	@echo "$(YELLOW)Credit Simulator - Comandos disponíveis:$(NC)"
	@echo "  $(GREEN)make help$(NC)        - Exibe esta ajuda"
	@echo "  $(GREEN)make clean$(NC)       - Limpa os arquivos compilados"
	@echo "  $(GREEN)make build$(NC)       - Compila e faz o build do projeto"
	@echo "  $(GREEN)make run$(NC)         - Executa a aplicação localmente"
	@echo "  $(GREEN)make test$(NC)        - Executa os testes unitários"
	@echo "  $(GREEN)make docker-build$(NC)- Constrói a imagem Docker"
	@echo "  $(GREEN)make docker-run$(NC)  - Executa a aplicação via Docker"
	@echo "  $(GREEN)make docker-stop$(NC) - Para os containers Docker"
	@echo "  $(GREEN)make test-age-rates$(NC)    - Testa taxas por faixa etária"
	@echo "  $(GREEN)make test-compare-rates$(NC)- Compara taxas fixas e variáveis"
	@echo "  $(GREEN)make test-currencies$(NC)   - Testa diferentes moedas"
	@echo "  $(GREEN)make test-bulk$(NC)         - Testa simulações em lote"
	@echo "  $(GREEN)make test-errors$(NC)       - Testa tratamento de erros"
	@echo "  $(GREEN)make test-all-api$(NC)      - Executa todos os testes da API"

clean:
	@echo "$(YELLOW)Limpando o projeto...$(NC)"
	@chmod +x ./scripts/*.sh
	@mvn clean
	@echo "$(GREEN)✅ Projeto limpo com sucesso!$(NC)"

build: clean
	@echo "$(YELLOW)Compilando e construindo o projeto...$(NC)"
	@mvn package -DskipTests
	@echo "$(GREEN)✅ Build concluído: $(JAR_FILE)$(NC)"

test:
	@echo "$(YELLOW)Executando testes unitários...$(NC)"
	@mvn test
	@echo "$(GREEN)✅ Testes concluídos!$(NC)"

run: build
	@echo "$(YELLOW)Executando a aplicação na porta $(PORT)...$(NC)"
	@java -jar $(JAR_FILE)

docker-build:
	@echo "$(YELLOW)Construindo imagem Docker...$(NC)"
	@docker build -t $(APP_NAME) .
	@echo "$(GREEN)✅ Imagem Docker construída com sucesso: $(APP_NAME)$(NC)"

docker-run: docker-build
	@echo "$(YELLOW)Executando aplicação via Docker...$(NC)"
	@docker-compose up --build -d
	@echo "$(GREEN)✅ Aplicação iniciada em http://localhost:$(PORT)$(NC)"

docker-stop:
	@echo "$(YELLOW)Parando os containers Docker...$(NC)"
	@docker-compose down
	@echo "$(GREEN)✅ Containers parados com sucesso!$(NC)"

test-age-rates:
	@echo "$(YELLOW)Testando taxas por faixa etária...$(NC)"
	@chmod +x ./scripts/test-age-rates.sh
	@./scripts/test-age-rates.sh

test-compare-rates:
	@echo "$(YELLOW)Comparando taxas fixas e variáveis...$(NC)"
	@chmod +x ./scripts/compare-rates.sh
	@./scripts/compare-rates.sh

test-currencies:
	@echo "$(YELLOW)Testando diferentes moedas...$(NC)"
	@chmod +x ./scripts/test-currencies.sh
	@./scripts/test-currencies.sh

test-bulk:
	@echo "$(YELLOW)Testando simulações em lote...$(NC)"
	@chmod +x ./scripts/test-bulk.sh
	@./scripts/test-bulk.sh

test-errors:
	@echo "$(YELLOW)Testando tratamento de erros...$(NC)"
	@chmod +x ./scripts/test-errors.sh
	@./scripts/test-errors.sh

test-all-api:
	@echo "$(YELLOW)Executando todos os testes de API...$(NC)"
	@chmod +x ./scripts/*.sh
	@./scripts/run-all-tests.sh

# Target padrão
default: help
