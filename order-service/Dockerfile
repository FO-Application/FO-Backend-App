# --- GIAI ĐOẠN 1: BUILD ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. Cài đặt Common Lib
COPY common-lib/pom.xml common-lib/pom.xml
COPY common-lib/src common-lib/src
RUN mvn -f common-lib/pom.xml clean install -DskipTests

# 2. Build Order Service
COPY order-service/pom.xml order-service/pom.xml
WORKDIR /app/order-service
RUN mvn dependency:go-offline

COPY order-service/src src
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# --- GIAI ĐOẠN 2: RUN ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/order-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]