# Simulador de Crédito

Uma aplicação Java com Spring Boot que calcula condições de pagamento de empréstimos baseado em diversos parâmetros.

## Índice

- [Simulador de Crédito](#simulador-de-crédito)
  - [Índice](#índice)
  - [Visão Geral](#visão-geral)
  - [Arquitetura](#arquitetura)
  - [Funcionalidades](#funcionalidades)
    - [Cálculo de Empréstimo](#cálculo-de-empréstimo)
    - [Taxas de Juros](#taxas-de-juros)
    - [Conversão de Moedas](#conversão-de-moedas)
    - [Alta Volumetria](#alta-volumetria)
  - [Configuração e Execução](#configuração-e-execução)
    - [Requisitos](#requisitos)
    - [Execução Local](#execução-local)
    - [Execução com Docker](#execução-com-docker)
      - [Método 1: Docker Compose](#método-1-docker-compose)
      - [Método 2: Comandos Docker](#método-2-comandos-docker)
      - [Método 3: Via Makefile](#método-3-via-makefile)
      - [Verificações](#verificações)
  - [API](#api)
    - [Endpoints](#endpoints)
      - [`POST /api/simulations/single`](#post-apisimulationssingle)
      - [`POST /api/simulations/bulk`](#post-apisimulationsbulk)
    - [Exemplos de Uso](#exemplos-de-uso)
    - [Tratamento de Erros](#tratamento-de-erros)
      - [Campos obrigatórios](#campos-obrigatórios)
  - [Testes](#testes)
    - [Testes Unitários](#testes-unitários)
    - [Scripts de Teste](#scripts-de-teste)
  - [Desenvolvimento](#desenvolvimento)
    - [Usando o Makefile](#usando-o-makefile)
    - [Decisões de Design](#decisões-de-design)
  - [Próximos Passos](#próximos-passos)

## Visão Geral

O Simulador de Crédito permite calcular condições de pagamento de empréstimos com base em:

- Valor solicitado
- Data de nascimento (determina a faixa etária e taxa de juros aplicável)
- Prazo de pagamento (em meses)
- Cenário de taxa (fixa ou variável)
- Moeda (BRL, USD, EUR)

O projeto suporta alta volumetria de cálculos, possui testes automatizados, documentação via OpenAPI/Swagger e execução containerizada.

## Arquitetura

A aplicação segue arquitetura em camadas:

- **Modelo (`model`)**: Entidades de entrada/saída da API e enums
- **Serviço (`service`)**: Lógica de negócio de simulação, cálculo, conversão e taxas
- **Controlador (`controller`)**: Endpoints RESTful
- **Configurações (`config`)**: Execução assíncrona e definição de executores
- **Exceções**: Tratamento global de erros

A documentação OpenAPI é gerada automaticamente pelo SpringDoc e acessível via `/swagger-ui.html`.

## Funcionalidades

### Cálculo de Empréstimo

A fórmula utilizada para cálculo das parcelas fixas é:

```math
PMT = \frac{PV \cdot r}{1 - (1 + r)^{-n}}
```

Onde:

- `PV`: valor presente (principal) em BRL
- `r`: taxa de juros mensal (taxa anual ÷ 12)
- `n`: número total de parcelas

### Taxas de Juros

As taxas variam conforme a faixa etária:

| Idade do cliente | Taxa anual fixa |
| ---------------- | --------------: |
| Até 25 anos      |              5% |
| De 26 a 40 anos  |              3% |
| De 41 a 60 anos  |              2% |
| Acima de 60 anos |              4% |

Para o cenário `VARIABLE`, adiciona-se 1% à taxa fixa como exemplo de ajuste variável.

### Conversão de Moedas

O sistema suporta BRL, USD e EUR. Para consistência, os valores são convertidos para BRL para cálculo e reconvertidos para a moeda original. Taxas estáticas definidas:

- 1 USD = 5 BRL
- 1 EUR = 6 BRL

### Alta Volumetria

O endpoint `/api/simulations/bulk` permite processar múltiplas simulações em uma única chamada, utilizando streams paralelos para distribuir o processamento.

## Configuração e Execução

### Requisitos

- JDK 17 ou superior
- Maven 3.9'+ (execução local)
- Docker (execução containerizada)

### Execução Local

1. Clone o repositório e acesse a pasta:

   ```bash
   cd credit-simulator
   ```

2. Compile o projeto:

   ```bash
   mvn clean verify
   ```

3. Inicie a aplicação:
   ```bash
   mvn spring-boot:run
   ```

A API estará disponível em `http://localhost:8080` e a documentação Swagger em `/swagger-ui.html`.

### Execução com Docker

#### Método 1: Docker Compose

```bash
docker-compose up --build
```

#### Método 2: Comandos Docker

```bash
docker build -t credit-simulator .
docker run -p 8080:8080 credit-simulator
```

#### Método 3: Via Makefile

```bash
make docker-stop  # Para remover containers existentes
make docker-run   # Para construir e iniciar a aplicação
```

#### Verificações

Para verificar se a aplicação está rodando:

```bash
docker ps
docker logs credit-simulator
```

Para logs mais detalhados, configure o nível de log para DEBUG no `docker-compose.yml`:

```yaml
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB: DEBUG
LOGGING_LEVEL_COM_EXAMPLE_CREDITSIMULATOR: DEBUG
```

## API

### Endpoints

A API segue padrão REST com requisições POST e corpo em JSON.

#### `POST /api/simulations/single`

Realiza simulação de um único empréstimo.

**Exemplo de requisição:**

```json
{
  "loanAmount": 10000.0,
  "dateOfBirth": "1990-05-20",
  "termMonths": 24,
  "interestRateScenario": "FIXED",
  "currency": "BRL"
}
```

**Exemplo de resposta:**

```json
{
  "totalPayableAmount": 10315.49,
  "monthlyInstallment": 429.81,
  "totalInterestPaid": 315.49,
  "annualInterestRate": 0.03,
  "currency": "BRL"
}
```

#### `POST /api/simulations/bulk`

Executa múltiplas simulações em uma única chamada.

**Exemplo de requisição:**

```json
[
  {
    "loanAmount": 5000.0,
    "dateOfBirth": "1985-02-10",
    "termMonths": 12,
    "interestRateScenario": "FIXED",
    "currency": "USD"
  },
  {
    "loanAmount": 15000.0,
    "dateOfBirth": "1975-11-30",
    "termMonths": 36,
    "interestRateScenario": "VARIABLE",
    "currency": "EUR"
  }
]
```

**Exemplo de resposta:**

```json
[
  {
    "totalPayableAmount": 5081.62,
    "monthlyInstallment": 423.47,
    "totalInterestPaid": 81.62,
    "annualInterestRate": 0.03,
    "currency": "USD"
  },
  {
    "totalPayableAmount": 15703.85,
    "monthlyInstallment": 436.22,
    "totalInterestPaid": 703.85,
    "annualInterestRate": 0.03,
    "currency": "EUR"
  }
]
```

### Exemplos de Uso

Para exemplos detalhados com curl para diferentes cenários:

- Diferentes faixas etárias
- Comparação entre taxas fixas e variáveis
- Diferentes moedas (BRL, USD, EUR)
- Diferentes prazos de pagamento
- Simulações em lote

Consulte a [documentação Swagger](http://localhost:8080/swagger-ui.html) quando a aplicação estiver em execução.

### Tratamento de Erros

A API valida e trata os seguintes erros:

- Valores de empréstimo negativos ou zero
- Moedas não suportadas
- Datas de nascimento inválidas
- Campos obrigatórios ausentes
- Prazos em meses inválidos
- Cenários de taxa de juros inválidos

#### Campos obrigatórios

- `loanAmount`: Valor do empréstimo (maior que zero)
- `dateOfBirth`: Data de nascimento do cliente (formato YYYY-MM-DD)
- `termMonths`: Prazo em meses
- `interestRateScenario`: Cenário de taxa de juros (FIXED ou VARIABLE)
- `currency`: Moeda (BRL, USD, EUR)

## Testes

### Testes Unitários

Execute os testes unitários com:

```bash
mvn test
```

Os testes cobrem:

- Cálculo de taxas por faixa etária
- Conversão de moedas
- Processamento em lote
- Validação de endpoints REST

### Scripts de Teste

Na pasta `/scripts` estão disponíveis scripts para testar diferentes aspectos:

```bash
# Teste de faixas etárias
./scripts/test-age-rates.sh

# Comparação de taxas fixas vs variáveis
./scripts/compare-rates.sh

# Teste de diferentes moedas
./scripts/test-currencies.sh

# Simulação em lote
./scripts/test-bulk.sh

# Tratamento de erros
./scripts/test-errors.sh
```

## Desenvolvimento

### Usando o Makefile

O projeto inclui um Makefile para facilitar operações comuns:

```bash
make help           # Ver comandos disponíveis
make clean          # Limpar o projeto
make build          # Compilar e fazer build
make run            # Executar a aplicação
make test           # Executar testes unitários
make test-all-api   # Executar todos os testes de API
```

### Decisões de Design

- **Separação de responsabilidades**: Regras de negócio encapsuladas em serviços específicos
- **Escalabilidade**: Uso de streams paralelos e design para processamento assíncrono
- **Internacionalização de moedas**: Conversão padronizada para cálculos consistentes
- **Documentação**: Integração com SpringDoc para documentação automática
- **Automação**: Makefile e scripts para simplificar testes e desenvolvimento

## Próximos Passos

Em um cenário real, recomenda-se:

- Integrar provedores de dados financeiros para taxas de câmbio e juros em tempo real
- Persistir simulações em banco de dados para auditoria e análise
- Implementar autenticação e autorização
- Utilizar mensageria (Kafka, RabbitMQ, SQS) para distribuição de processamento

---

Este simulador foi desenvolvido como demonstração de habilidades em Java, Spring Boot, design de APIs RESTful e testes automatizados.