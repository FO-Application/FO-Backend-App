# 🛠️ Food Ordering Backend - Developer Guide

Tài liệu hướng dẫn chi tiết quy trình **Build (Maven)** và **Deploy (Docker)** dành cho môi trường Development.

---

## 📋 1. Yêu cầu hệ thống (Prerequisites)

Trước khi bắt đầu, hãy đảm bảo máy của bạn đã cài đặt:

- **Java JDK:** Phiên bản 21 trở lên.
- **Maven:** Đã cài đặt (hoặc sử dụng `mvnw` từ project).
- **Docker & Docker Desktop:** Đang chạy ổn định.

---

## 🚧 2. Build Code (Maven)

Trước khi chạy Docker, bạn **bắt buộc** phải build code Java thành file `.jar`.

### 🟢 Cách 1: Build Chuẩn (Khuyên dùng hằng ngày)

```powershell
# Chạy tại thư mục gốc
.\mvnw clean package -DskipTests -Dfile.encoding=UTF-8
```

### 🔴 Cách 2: Force Re-build (Khi đổi dependency hoặc lỗi thư viện)

```powershell
.\mvnw clean install -U -DskipTests -Dfile.encoding=UTF-8
```

### 🟡 Cách 3: Build riêng lẻ một Service

```powershell
cd user-service
..\mvnw clean package -DskipTests -Dfile.encoding=UTF-8
```

---

## 🐳 3. Chạy Docker (Deployment)

### 🚀 Khởi động toàn bộ hệ thống

```powershell
docker-compose up -d --build
```

### 🔄 Cập nhật code cho 1 Service cụ thể

```powershell
docker-compose up -d --build --force-recreate user-service
```

### 🛑 Dừng hệ thống

```powershell
docker-compose stop
```

### 🧹 Reset sạch Database & Cache (Cẩn thận!)

```powershell
docker-compose down -v
```

---

## ⚠️ 4. Lỗi thường gặp & Cách xử lý nhanh

### 🛑 1. Gateway 503

```powershell
docker restart fo-api-gateway
```

### 🛑 2. TLS handshake timeout

→ Restart Docker Desktop và chạy lại lệnh.

### 🛑 3. JDBC Connection Failed

```powershell
docker restart fo-user-service
```

### 🛑 4. Redis không lưu dữ liệu

```powershell
docker-compose up -d --force-recreate user-service
```

---

## 🎉 Kết thúc

**Chúc bạn coding vui vẻ & không gặp bug! 🚀**
