# Food Ordering Backend

Hệ thống backend cho ứng dụng đặt đồ ăn trực tuyến, được xây dựng theo kiến trúc **Microservices** với khả năng mở rộng và hiệu năng cao.

---

## Mục lục

1. [Công nghệ sử dụng](#công-nghệ-sử-dụng)
2. [Kiến trúc hệ thống](#kiến-trúc-hệ-thống)
3. [Cấu trúc dự án](#cấu-trúc-dự-án)
4. [Chi tiết các Microservices](#chi-tiết-các-microservices)
5. [API Endpoints](#api-endpoints)
6. [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
7. [Hướng dẫn cài đặt và chạy](#hướng-dẫn-cài-đặt-và-chạy)
8. [Cấu hình môi trường](#cấu-hình-môi-trường)
9. [Các cổng mặc định](#các-cổng-mặc-định)
10. [Luồng hoạt động chính](#luồng-hoạt-động-chính)
11. [Kafka Event-Driven](#kafka-event-driven)
12. [Các lỗi thường gặp](#các-lỗi-thường-gặp)

---

## Công nghệ sử dụng

| Thành phần | Công nghệ |
|------------|-----------|
| Ngôn ngữ | Java 21 |
| Framework | Spring Boot 3.3.5 |
| Microservices | Spring Cloud Netflix (Eureka), Spring Cloud Gateway, OpenFeign |
| Messaging | Apache Kafka |
| Database | MySQL 8.0 |
| Caching & Geo | Redis |
| Object Storage | MinIO |
| Payment Gateway | ZaloPay |
| Push Notification | Firebase Cloud Messaging (FCM) |
| Containerization | Docker, Docker Compose |
| Tunnel | Ngrok |
| Build Tool | Maven |

---

## Kiến trúc hệ thống

```
                                    ┌─────────────────┐
                                    │     Ngrok       │
                                    │   (Tunnel)      │
                                    └────────┬────────┘
                                             │
                                    ┌────────▼────────┐
                                    │   API Gateway   │
                                    │   (Port 8080)   │
                                    └────────┬────────┘
                                             │
                    ┌─────────────────┬──────┼──────┬─────────────────┐
                    │                 │      │      │                 │
           ┌────────▼────────┐ ┌──────▼──────┐ ┌────▼────┐ ┌──────────▼──────────┐
           │  User Service   │ │   Order     │ │Merchant │ │   Delivery Service  │
           │                 │ │  Service    │ │ Service │ │                     │
           │    (:8081)      │ │   (:8084)   │ │ (:8083) │ │       (:8085)       │
           └────────┬────────┘ └──────┬──────┘ └────┬────┘ └──────────┬──────────┘
                    │                 │             │                 │
           ┌────────▼─────────────────▼─────────────▼─────────────────▼────────┐
           │                         Apache Kafka                              │
           └────────┬─────────────────┬─────────────┬─────────────────┬────────┘
                    │                 │             │                 │
           ┌────────▼────────┐ ┌──────▼──────┐ ┌────▼────┐ ┌──────────▼──────────┐
           │  Notification   │ │   Payment   │ │  MySQL  │ │        Redis        │
           │    Service      │ │   Service   │ │         │ │       (Cache)       │
           │    (:8082)      │ │   (:8086)   │ │         │ │                     │
           └─────────────────┘ └─────────────┘ └─────────┘ └─────────────────────┘
```

---

## Cấu trúc dự án

```
food-ordering-backend/
├── api-gateway/              # API Gateway - Cổng vào duy nhất
├── discovery-server/         # Eureka Server - Service Registry
├── user-service/             # Quản lý người dùng, Auth, OTP
├── order-service/            # Quản lý đơn hàng, tính phí ship
├── merchant-service/         # Quản lý nhà hàng, món ăn, ví merchant
├── delivery-service/         # Quản lý giao hàng, shipper, ví shipper
├── payment-service/          # Tích hợp thanh toán ZaloPay
├── notification-service/     # Gửi email và push notification
├── common-lib/               # Thư viện dùng chung (DTO, Exception)
├── mysql-init/               # Script khởi tạo database
├── minio_data/               # Dữ liệu MinIO (Object Storage)
├── docker-compose.yaml       # Cấu hình Docker Compose
├── .env                      # Biến môi trường
└── README.md
```

---

## Chi tiết các Microservices

### 1. API Gateway (Port 8080)
Cổng vào duy nhất của hệ thống, xử lý Routing, Auth Filter (Cookie -> Header), và Rate Limiting.

### 2. Discovery Server (Port 8761)
Eureka Server cho phép các service tự động đăng ký và tìm kiếm nhau.

### 3. User Service (Port 8081)
- Authentication (Login, Register, OTP).
- Quản lý User Profile.
- Tích hợp Firebase Auth (Google/Facebook Login).

### 4. Order Service (Port 8084)
- Tạo và quản lý trạng thái đơn hàng.
- Tính phí ship dựa trên tọa độ.
- Xử lý Review (đánh giá).

Trạng thái đơn hàng:
```
CREATED -> PAID -> PREPARING -> READY -> PICKED_UP -> DELIVERING -> COMPLETED
   Start                    Merchant      Shipper       Shipper       End
   |                                                                   |
   └-> CANCELED (Khách hoặc Quán hủy)                                  |
```

### 5. Merchant Service (Port 8083)
- Quản lý Nhà hàng, Menu, Topping.
- Upload ảnh (MinIO).
- Quản lý doanh thu (Ví Merchant).

### 6. Delivery Service (Port 8085)
- Tìm kiếm shipper gần nhất (Redis GEO).
- Quản lý trạng thái giao vận.
- Quản lý thu nhập Shipper (Ví Shipper).

### 7. Payment Service (Port 8086)
- Tích hợp ZaloPay (Tạo đơn, Callback).
- Xử lý hoàn tiền (Refund - *Planned*).

### 8. Notification Service (Port 8082)
- Central Hub nhận message từ Kafka.
- Gửi Email (JavaMailSender).
- Gửi Push Notification (Firebase Cloud Messaging).

---

## API Endpoints

### User Service (`/api/v1/auth`, `/api/v1/user`)
- POST `/auth/register/customer`: Đăng ký
- POST `/auth/login`: Đăng nhập
- POST `/auth/login/firebase`: Đăng nhập Social (Google/FB)

### Order Service (`/api/v1/order`)
- POST `/`: Tạo đơn hàng
- GET `/`: Lịch sử đơn hàng
- PATCH `/{id}/cancel`: Hủy đơn

### Merchant Management (`/api/v1/management/order/merchant`)
- PUT `/{id}/confirm`: Nhận đơn
- PUT `/{id}/ready`: Báo món xong

### Delivery Service (`/api/v1/delivery/shippers`)
- POST `/location`: Cập nhật vị trí
- POST `/accept`: Nhận đơn
- POST `/picked-up`: Đã lấy hàng
- POST `/complete`: Đã giao xong

### Payment Service (`/api/v1/payment`)
- POST `/zalopay/create`: Tạo thanh toán
- POST `/callback`: Webhook ZaloPay

---

## Yêu cầu hệ thống

1. **Java JDK 21**
2. **Maven**
3. **Docker & Docker Desktop**

---

## Hướng dẫn cài đặt và chạy

### 1. Build Source Code
```powershell
.\mvnw clean package -DskipTests -Dfile.encoding=UTF-8
```

### 2. Khởi động hệ thống
```powershell
docker-compose up -d --build
```
Lệnh này sẽ khởi động toàn bộ stack gồm: MySQL, Redis, Kafka, MinIO, Zookeeper và 7 Microservices.

### 3. Dừng hệ thống
```powershell
docker-compose down -v
```
(Thêm `-v` để xóa sạch dữ liệu database/cache nếu muốn reset)

---

## Cấu hình môi trường

Các cổng quan trọng trong file `.env`:

| Biến | Giá trị mặc định | Mô tả |
|------|-----------------|-------|
| `GATEWAY_PORT` | 8080 | Cổng Public API |
| `USER_SERVICE_PORT` | 8081 | |
| `NOTIFICATION_SERVICE_PORT` | 8082 | |
| `MERCHANT_SERVICE_PORT` | 8083 | |
| `ORDER_SERVICE_PORT` | 8084 | |
| `DELIVERY_SERVICE_PORT` | 8085 | |
| `PAYMENT_SERVICE_PORT` | 8086 | |

---

## Luồng hoạt động chính

Xem chi tiết trong [SYSTEM_FLOW.md](./SYSTEM_FLOW.md).

1. **Đăng ký**: User -> User Service (Validate) -> Kafka -> Notification Service (Mail OTP).
2. **Đặt hàng**: User -> Order Service (Create) -> Kafka -> Notification (Push Merchant).
3. **Tìm xe**: Merchant (Confirm) -> Order Service -> Kafka -> Delivery Service (Find Shipper).
4. **Giao hàng**: Shipper (Accept -> PickedUp -> Complete) -> Order Service -> Kafka -> Merchant/Notification Service.

---

## Kafka Event-Driven

Hệ thống sử dụng 11 Topics chính để điều phối hoạt động.
Xem chi tiết cấu trúc Event trong [KAFKA_EVENT_FLOWS.md](./KAFKA_EVENT_FLOWS.md).

| Topic | Producer | Consumer |
|-------|----------|----------|
| `order-created-topic` | Order | Notification |
| `order-confirmed-topic` | Order | Delivery |
| `shipper-found-topic` | Delivery | Notification |
| `shipper-assigned-topic`| Delivery | Notification |
| `order-ready-topic` | Order | Notification |
| `order-completed-topic` | Order | Merchant |
| ... | ... | ... |

---

## Các lỗi thường gặp

1. **Lỗi Gateway 503**: Service chưa khởi động xong. Đợi 1-2 phút hoặc check log Eureka (http://localhost:8761).
2. **Lỗi Connection Refused**: Controller chưa connect được Database hoặc Kafka. Check `docker ps`.
3. **ZaloPay Callback lỗi**: Kiểm tra Ngrok domain trong `.env` có khớp với domain đang chạy không.

---

Cập nhật lần cuối: Tháng 01/2026
