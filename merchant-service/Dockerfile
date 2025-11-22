# --- GIAI ĐOẠN 1: BUILD ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1. CHỈ Copy file pom.xml vào trước
COPY pom.xml .

# 2. Tải thư viện về (Bước này sẽ được Docker CACHE lại nếu pom.xml không đổi)
# Lệnh này chỉ tải thư viện, không build code
RUN mvn dependency:go-offline

# 3. Bây giờ mới Copy source code vào
COPY src ./src

# 4. Build (Lúc này đã có sẵn thư viện, chỉ việc compile code thôi -> Rất nhanh)
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# --- GIAI ĐOẠN 2: RUN (Giữ nguyên) ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]