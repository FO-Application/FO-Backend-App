# --- GIAI ĐOẠN 1: BUILD ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. Cài đặt Common Lib
COPY common-lib/pom.xml common-lib/pom.xml
COPY common-lib/src common-lib/src
RUN mvn -f common-lib/pom.xml clean install -DskipTests

# 2. Build Merchant Service
COPY merchant-service/pom.xml merchant-service/pom.xml
WORKDIR /app/merchant-service
RUN mvn dependency:go-offline

COPY merchant-service/src src
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# --- GIAI ĐOẠN 2: RUN ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/merchant-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]