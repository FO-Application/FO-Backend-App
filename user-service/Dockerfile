# 1. Dùng Java 21 (Phù hợp với project của bạn)
FROM eclipse-temurin:21-jdk-alpine

# 2. Tạo thư mục làm việc
WORKDIR /app

# 3. Copy file JAR vào container
# Vì file này nằm trong thư mục code nào, nó sẽ tự tìm folder /target của thư mục đó
COPY target/*.jar app.jar

# 4. Chạy
ENTRYPOINT ["java", "-jar", "app.jar"]