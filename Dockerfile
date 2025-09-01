### Etapa de build: compila o projeto
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src

### Etapa de runtime: roda o artefato gerado
FROM eclipse-temurin:17-jre
LABEL maintainer="example@domain.com"
WORKDIR /app

# Copia somente o jar gerado da fase anterior
COPY --from=builder /build/target/credit-simulator-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Comando de entrada da aplicação
ENTRYPOINT ["java","-jar","/app/app.jar"]
