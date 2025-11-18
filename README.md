# 🛠️ Hướng dẫn Build & Chạy Dự án (Food Ordering Backend)

Tài liệu này hướng dẫn chi tiết cách **build code bằng Maven** và **triển khai hệ thống bằng Docker** dành cho môi trường Development.

## 📋 1. Tiền đề (Prerequisites)

Bạn cần chuẩn bị trước:

- **Java JDK:** 21 trở lên
- **Maven:** hoặc dùng `mvnw` có sẵn
- **Docker & Docker Desktop:** đang chạy bình thường

---

## 🚧 2. Build Code (Maven)

Trước khi chạy Docker, bạn **bắt buộc** phải build code Java thành file `.jar` mới nhất.

### 🟢 Cách 1 – Build toàn bộ project (Khuyên dùng)

Chạy trong Terminal tại thư mục gốc:

```
.\mvnw clean package -DskipTests -Dfile.encoding=UTF-8

# Hoặc dùng Maven trên máy
mvn clean package -DskipTests -Dfile.encoding=UTF-8
```

### 🟡 Cách 2 – Build chỉ một microservice

```
cd user-service
..\mvnw clean package -DskipTests -Dfile.encoding=UTF-8
```

---

## 🐳 3. Chạy Docker

### 🚀 Khởi động hệ thống

```
docker-compose up -d --build
```

### 🔄 Khởi động lại một service cụ thể

```
docker-compose up -d --build user-service
```

### 🛑 Dừng hệ thống

```
docker-compose stop
```

### 🧹 Reset Database

```
docker-compose down -v
```

---

## ⚠️ 4. Lỗi thường gặp & Cách xử lý nhanh

### 1. Gateway 503

```
docker restart fo-api-gateway
```

### 2. TLS handshake timeout

=> Restart Docker Desktop và chạy lại.

### 3. JDBC connection failed

```
docker restart fo-user-service
```

---

## 🎉 Kết thúc

**Chúc bạn coding vui vẻ & không gặp bug! 🚀**
