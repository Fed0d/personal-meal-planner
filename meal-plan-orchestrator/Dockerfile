FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

COPY pom.xml .
COPY jwt-security-spring-boot-starter/pom.xml jwt-security-spring-boot-starter/
COPY auth-service/pom.xml auth-service/
COPY user-service/pom.xml user-service/
COPY meal-service/pom.xml meal-service/
COPY meal-plan-service/pom.xml meal-plan-service/
COPY meal-plan-orchestrator/pom.xml meal-plan-orchestrator/
COPY api-gateway/pom.xml api-gateway/

RUN mvn dependency:go-offline -pl meal-plan-orchestrator -am -q || true

COPY jwt-security-spring-boot-starter/src jwt-security-spring-boot-starter/src
COPY meal-plan-orchestrator/src meal-plan-orchestrator/src

RUN mvn package -pl meal-plan-orchestrator -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/meal-plan-orchestrator/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
