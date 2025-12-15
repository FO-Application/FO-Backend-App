# 🍔 Food Ordering Backend

Chào mừng bạn đến với dự án **Food Ordering Backend**! Đây là hệ thống backend mạnh mẽ được xây dựng theo kiến trúc **Microservices**, cung cấp nền tảng cho ứng dụng đặt đồ ăn trực tuyến với khả năng mở rộng và hiệu năng cao.

---

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

Dự án sử dụng các công nghệ hiện đại nhất trong hệ sinh thái Java & Cloud Native:

-   **Core**: Java 21, Spring Boot 3.3.5
-   **Microservices**: Spring Cloud Netflix (Eureka, OpenFeign), Spring Cloud Gateway
-   **Messaging**: Apache Kafka (Event-driven architecture)
-   **Database**: MySQL 8.0 (Relational Data), Redis (Caching)
-   **Storage**: MinIO (Object Storage cho hình ảnh/file)
-   **Containerization**: Docker, Docker Compose
-   **Build Tool**: Maven

---

## 📂 Cấu Trúc Dự Án

Hệ thống bao gồm các module chính sau:

-   **`api-gateway`**: Cổng vào duy nhất của hệ thống, xử lý routing và security cơ bản.
-   **`discovery-server`**: Service Registry (Eureka Server) để các service tìm thấy nhau.
-   **`user-service`**: Quản lý thông tin người dùng, xác thực và phân quyền.
-   **`merchant-service`**: Quản lý nhà hàng, món ăn và menu.
-   **`notification-service`**: Xử lý gửi email/thông báo (tích hợp Kafka).
-   **`common-lib`**: Thư viện chia sẻ các DTO, Exception và Utility dùng chung.

---

## 📋 Yêu Cầu Hệ Thống (Prerequisites)

Để chạy dự án, máy tính của bạn cần cài đặt sẵn:

1.  **Java JDK 21**: [Tải tại đây](https://www.oracle.com/java/technologies/downloads/#java21)
2.  **Maven**: Đã cài đặt (hoặc dùng `mvnw` có sẵn trong dự án).
3.  **Docker & Docker Desktop**: [Tải tại đây](https://www.docker.com/products/docker-desktop/)

---

## � Hướng Dẫn Cài Đặt & Chạy (Docker)

Đây là cách nhanh nhất để dựng toàn bộ hệ thống backend.

### 1. Build Source Code
Trước khi chạy Docker, bạn cần build code Java thành file `.jar`.

```powershell
# Tại thư mục gốc của dự án
.\mvnw clean package -DskipTests -Dfile.encoding=UTF-8
```

### 2. Khởi Động Hệ Thống (Docker Up)
Lệnh này sẽ tạo và chạy các container (MySQL, Redis, Kafka, Zookeeper, MinIO và các Microservices).

```powershell
docker-compose up -d --build
```
*   `-d`: Chạy ngầm (detached mode).
*   `--build`: Build lại image nếu có thay đổi trong Dockerfile.

### 3. Dừng Hệ Thống (Docker Stop)
Để tạm dừng các container mà không xóa dữ liệu:

```powershell
docker-compose stop
```

### 4. Xóa Sạch & Reset (Docker Down -v) ⚠️
Lệnh này rất quan trọng khi bạn muốn **reset hoàn toàn** hệ thống về trạng thái ban đầu. Nó sẽ:
-   Dừng và xóa các container.
-   **Xóa các Volume**: Dữ liệu trong MySQL, Redis, MinIO sẽ bị mất hết.

Dùng lệnh này khi bạn muốn cài đặt lại từ đầu hoặc khi gặp lỗi dữ liệu không đồng bộ.

```powershell
docker-compose down -v
```

### 5. Cập Nhật Code Cho 1 Service Cụ Thể
Khi bạn chỉ sửa code của một service (ví dụ `user-service`) và muốn cập nhật lại container của nó mà không restart cả hệ thống:

```powershell
# 1. Build lại file .jar của service đó
cd user-service
..\mvnw clean package -DskipTests -Dfile.encoding=UTF-8
cd ..

# 2. Re-create container của service đó
docker-compose up -d --build --force-recreate user-service
```

---

## ⚠️ Các Lỗi Thường Gặp & Cách Fix

| Lỗi | Nguyên nhân | Cách xử lý |
| :--- | :--- | :--- |
| **Gateway 503 Service Unavailable** | Service chưa kịp đăng ký với Eureka hoặc chưa start xong. | Chờ 1-2 phút hoặc restart gateway: `docker restart fo-api-gateway` |
| **Connection Refused (DB/Kafka)** | Container DB/Kafka chưa sẵn sàng. | Restart service bị lỗi: `docker restart fo-user-service` |
| **Redis không lưu dữ liệu** | Volume Redis bị lỗi hoặc đầy. | Reset Redis: `docker-compose up -d --force-recreate redis-cache` |
| **Lỗi build Maven** | Thiếu thư viện hoặc lỗi mạng. | Thử force update: `.\mvnw clean install -U -DskipTests` |

---

## 🔗 Các Cổng Mặc Định (Ports)

| Service | Port | Mô tả |
| :--- | :--- | :--- |
| **API Gateway** | `8222` | Cổng chính để gọi API |
| **Discovery Server** | `8761` | Dashboard Eureka |
| **MinIO Console** | `9001` | Quản lý file/ảnh |
| **MySQL** | `3306` | Database chính |
| **Redis** | `6379` | Cache |

---

**Happy Coding! 👨‍�**
