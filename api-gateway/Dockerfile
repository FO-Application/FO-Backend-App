# --- GIAI ĐOẠN 1: BUILD (Dùng Maven để đóng gói JAR) ---
# Dùng image có sẵn Maven và JDK
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Tạo thư mục làm việc
WORKDIR /app

# Copy toàn bộ source code vào trong Docker (bao gồm pom.xml và src)
COPY . .

# Chạy lệnh build ngay bên trong Docker
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# --- GIAI ĐOẠN 2: RUN (Chỉ lấy file JAR để chạy cho nhẹ) ---
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy file JAR từ "GIAI ĐOẠN 1" sang đây
# Lưu ý: đường dẫn target/*.jar phải khớp với cấu trúc maven
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]